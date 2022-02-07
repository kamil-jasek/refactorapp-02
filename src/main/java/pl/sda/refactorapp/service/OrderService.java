package pl.sda.refactorapp.service;

import static java.util.UUID.randomUUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import pl.sda.refactorapp.annotation.Inject;
import pl.sda.refactorapp.annotation.Service;
import pl.sda.refactorapp.annotation.Transactional;
import pl.sda.refactorapp.dao.DiscountCouponsDao;
import pl.sda.refactorapp.dao.OrderDao;
import pl.sda.refactorapp.entity.DiscountCoupon;
import pl.sda.refactorapp.entity.Item;
import pl.sda.refactorapp.entity.Order;

@Service
public class OrderService {

    @Inject
    private CustomerService customerService;

    @Inject
    private DiscountCouponsDao couponsDao;

    @Inject
    private OrderDao dao;

    @Transactional
    public boolean makeOrder(MakeOrderForm form) {
        final var maybeCustomer = customerService.findById(form.getCustomerId());
        if (maybeCustomer.isEmpty() || !form.hasValidItems()) {
            return false;
        }
        final var order = Order.createFrom(form);
        couponsDao.findByCode(form.getCoupon())
            .ifPresent(discountCoupon -> applyDiscount(order, discountCoupon));
        order.computeDelivery();
        dao.save(order);
        return sendEmail(maybeCustomer.get().getEmail(),
            "Your order is placed!",
            "Thanks for ordering our products. Your order will be send very soon!");
    }

    private void applyDiscount(Order order, DiscountCoupon discountCoupon) {
        if (!discountCoupon.isUsed()) {
            order.setDiscount(discountCoupon.getValue());
            discountCoupon.setUsedBy(order.getCid());
            discountCoupon.setUsed(true);
            couponsDao.save(discountCoupon);
        }
    }

    /**
     * Create order and apply provided discount
     * @param cid
     * @param items
     * @param discount
     * @return
     */
    @Transactional
    public boolean makeOrder(UUID cid, List<Item> items, float discount) {
        var result = false;
        var optional = customerService.findById(cid);
        if (optional.isPresent() && items != null && items.size() > 0 && discount > 0 && discount < 1) {
            // validate items
            for (var i : items) {
                if (i.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
                    i.getWeight() <= 0 ||
                    i.getQuantity() < 1) {
                    return false;
                }
            }

            var order = new Order();
            order.setId(randomUUID());
            order.setCid(cid);
            order.setCtime(LocalDateTime.now());
            order.setStatus(Order.ORDER_STATUS_WAITING);
            order.setDiscount(discount);
            var itemsList = order.getItems();
            if (itemsList == null) {
                itemsList = new ArrayList<>();
            }
            itemsList.addAll(items);
            order.setItems(itemsList);

            order.computeDelivery();

            // save to db
            dao.save(order);

            // send email
            final var sendEmail = sendEmail(optional.get().getEmail(),
                "Your order is placed!",
                "Thanks for ordering our products. Your order will be send very soon!");
            result = sendEmail;
        }

        return result;
    }

    /**
     * Change order status
     * @param oid
     * @param status
     * @return
     */
    @Transactional
    public boolean updateOrderStatus(UUID oid, int status) {
        var result = false;
        var optional = dao.findById(oid);
        if (optional.isPresent() && status > 0 && status < 4) {
            var order = optional.get();
            if (status - order.getStatus() == 1) {
                order.setStatus(status);
                dao.save(order);
                var customer = customerService.findById(order.getCid()).get();
                var emailSend = false;
                if (status == 2) {
                    emailSend = sendEmail(customer.getEmail(),
                        "Order status updated to sent",
                        "Your order changed status to sent. Our courier will deliver your order in 2 business days.");
                } else if (status == 3) {
                    emailSend = sendEmail(customer.getEmail(),
                        "Order status updated to delivered",
                        "Your order changed status to delivered. Thank you for ordering our products!");
                }

                result = emailSend;
            }
        }
        return result;
    }

    private boolean sendEmail(String address, String subj, String msg) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", System.getenv().get("MAIL_SMTP_HOST"));
        prop.put("mail.smtp.port", System.getenv().get("MAIL_SMTP_PORT"));
        prop.put("mail.smtp.ssl.trust", System.getenv().get("MAIL_SMTP_SSL_TRUST"));

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "admin");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@company.com"));
            message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(address));
            message.setSubject(subj);

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
