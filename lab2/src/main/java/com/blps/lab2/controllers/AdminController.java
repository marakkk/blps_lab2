package com.blps.lab2.controllers;

import com.blps.lab2.dto.AppDto;
import com.blps.lab2.entities.googleplay.App;
import com.blps.lab2.entities.payments.Payment;
import com.blps.lab2.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin-actions")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_CREATE')")
    @PostMapping("/apps")
    public ResponseEntity<App> createApp(@RequestBody AppDto appDto) {
        App createdApp = adminService.createApp(appDto);
        return ResponseEntity.ok(createdApp);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_UPDATE')")
    @PutMapping("/apps/{appId}")
    public ResponseEntity<App> updateApp(@PathVariable Long appId, @RequestBody AppDto appDto) {
        App updatedApp = adminService.updateApp(appId, appDto);
        return ResponseEntity.ok(updatedApp);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_DELETE')")
    @DeleteMapping("/app/{appId}")
    public ResponseEntity<String> deleteApp(@PathVariable Long appId) throws NamingException {
        adminService.deleteApp(appId);
        return ResponseEntity.ok("App successfully deleted.");
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('PAYMENT_CREATE')")
    @PostMapping("/payments")
    public ResponseEntity<Payment> createPayment(@RequestParam Long appId, @RequestParam double amount) {
        Payment payment = adminService.createPayment(appId, amount);
        return ResponseEntity.ok(payment);
    }

    @PreAuthorize("hasRole('ADMIN') and hasAuthority('APP_MODERATE')")
    @PostMapping("/apps/{appId}/moderate")
    public ResponseEntity<Map<String, String>> moderateApp(@PathVariable Long appId,
                                                           @RequestParam boolean approved,
                                                           @RequestParam String moderatorComment) {
        Map<String, String> response = adminService.moderateApp(appId, approved, moderatorComment);
        return ResponseEntity.ok(response);
    }
}
