package com.blps.lab2.services;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.blps.lab2.dto.AppDto;
import com.blps.lab2.entities.googleplay.App;
import com.blps.lab2.entities.payments.Payment;
import com.blps.lab2.enums.AppStatus;
import com.blps.lab2.enums.PaymentStatus;
import com.blps.lab2.repo.googleplay.AppRepository;
import com.blps.lab2.repo.payments.PaymentRepository;
import jakarta.transaction.UserTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AppRepository appRepository;
    private final PaymentRepository paymentRepository;
    private final UserTransactionManager userTransaction;

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_CREATE')")
    public App createApp(AppDto appDto) {
        try {
            userTransaction.begin();

            App app = new App();
            app.setName(appDto.getName());
            app.setVersion(appDto.getVersion());
            app.setStatus(AppStatus.PENDING);
            app.setDownloads(0);
            app.setRevenue(0);
            app.setInAppPurchases(appDto.isInAppPurchases());
            app.setNotFree(appDto.isNotFree());
            app.setAppPrice(appDto.getAppPrice());
            app.setMonetizationType(appDto.getMonetizationType());

            app = appRepository.save(app);

            userTransaction.commit();
            return app;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_UPDATE')")
    public App updateApp(Long appId, AppDto appDto) {
        try {
            userTransaction.begin();

            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            app.setName(appDto.getName());
            app.setVersion(appDto.getVersion());
            app.setInAppPurchases(appDto.isInAppPurchases());
            app.setNotFree(appDto.isNotFree());
            app.setAppPrice(appDto.getAppPrice());
            app.setMonetizationType(appDto.getMonetizationType());

            app = appRepository.save(app);

            userTransaction.commit();
            return app;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_DELETE')")
    public void deleteApp(Long appId) {
        try {
            userTransaction.begin();

            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            appRepository.delete(app);

            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PAYMENT_CREATE')")
    public Payment createPayment(Long appId, double amount) {
        try {
            userTransaction.begin();

            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            Payment payment = new Payment();
            payment.setAppId(appId);
            payment.setAmount(amount);
            payment.setStatus(PaymentStatus.SUCCESS);

            paymentRepository.save(payment);

            userTransaction.commit();
            return payment;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_MODERATE')")
    public Map<String, String> moderateApp(Long appId, boolean approved, String moderatorComment) {
        try {
            userTransaction.begin();

            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            Map<String, String> response = new HashMap<>();
            if (approved) {
                app.setStatus(AppStatus.APPROVED);
                response.put("message", "App approved by moderator.");
            } else {
                app.setStatus(AppStatus.REJECTED);
                response.put("reason", moderatorComment);
            }

            appRepository.save(app);

            userTransaction.commit();
            return response;
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }
}