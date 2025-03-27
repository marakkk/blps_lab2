package com.blps.lab2.services;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.blps.lab2.dto.AppDto;
import com.blps.lab2.entities.googleplay.App;
import com.blps.lab2.entities.googleplay.AppUser;
import com.blps.lab2.entities.payments.Payment;
import com.blps.lab2.enums.PaymentStatus;
import com.blps.lab2.repo.googleplay.AppRepository;
import com.blps.lab2.repo.googleplay.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AppUserService {

    private final UserRepository userRepository;
    private final AppRepository appRepository;
    private final PaymentService paymentService;
    private final UserTransactionManager userTransaction;


    @PreAuthorize("hasRole('USER') and hasAuthority('APP_CATALOG')")
    public List<AppDto> viewAppCatalog() {
        return appRepository.findAll().stream()
                .map(app -> new AppDto(
                        app.getId(),
                        app.getName(),
                        app.getVersion(),
                        app.getStatus(),
                        app.getDownloads(),
                        app.getRevenue(),
                        app.isInAppPurchases(),
                        app.isNotFree(),
                        app.getAppPrice(),
                        app.getMonetizationType()
                ))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER') and hasAuthority('APP_DOWNLOAD')")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String downloadApp(Long userId, Long appId) {
        try {
            userTransaction.begin();

            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            if (app.isNotFree()) {
                Payment payment = paymentService.payForApp(userId, appId);
                if (payment.getStatus() != PaymentStatus.SUCCESS) {
                    userTransaction.rollback();
                    return "Payment for app failed.";
                }
            }

            userTransaction.commit();
            return "User " + user.getUsername() + " successfully downloaded " + app.getName() + ".";
        } catch (Exception e) {
            if (userTransaction != null) {
                try {
                    userTransaction.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('USER') and hasAuthority('APP_USE')")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String useApp(Long userId, Long appId) {
        try {
            userTransaction.begin();

            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            App app = appRepository.findById(appId)
                    .orElseThrow(() -> new IllegalArgumentException("App not found"));

            String result = "User " + user.getUsername() + " started using " + app.getName() + ".";

            if (app.isInAppPurchases()) {
                result += "\nIn-app purchases detected.";
                Payment inAppPayment = paymentService.payForInAppPurchase(userId, appId);
                if (inAppPayment.getStatus() == PaymentStatus.SUCCESS) {
                    result += "\nUser purchased in-app content and continues using the app.";
                } else {
                    result += "\nUser could not purchase in-app content.";
                }
            }

            userTransaction.commit();
            return result;
        } catch (Exception e) {
            if (userTransaction != null) {
                try {
                    userTransaction.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }
}

