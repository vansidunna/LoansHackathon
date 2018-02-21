package com.example.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
public class FrontendController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private LoanRequestRepo loanRequestRepo;

    @Autowired
    private BankLoanRequestRepo bankLoanRequestRepo;

    @Data
    public static class Login {
        private String user;
        private String pass;
    }

    @Data
    public static class BankLoanRequestStatus {
        private Long id;
        private Long amount;
        private String purpose;
        private Long approvedAmount;
        private Long interestRate;
        private String comments;
    }

    private Optional<String> getUserName(HttpServletRequest request) {
        Cookie[] x = request.getCookies();

        for (int i=0; x != null && i<x.length; i++) {
            if (x[i].getName().equals("user")) {
                return Optional.of(x[i].getValue());
            }
        }
        return Optional.empty();
    }

    @GetMapping("/")
    public String root(HttpServletRequest request, Model model) {
        Optional<String> userName = getUserName(request);
        if (!userName.isPresent()) {
            return "login";
        }
        Optional<Customer> customer = customerRepo.fetchCustomer(userName.get());
        if (!customer.isPresent()) {
            Customer c1 = Customer.builder().id(userName.get()).build();
            model.addAttribute("customer", c1);
            return "me";
        } else {
            // Fake
            model.addAttribute("customer", customer.get());
            return "redirect:/loan";
        }
    }

    @PostMapping("/login")
    public ModelAndView login(HttpServletResponse response, @ModelAttribute Login login) {
        log.info("login({})", login);
        ModelAndView modelAndView = new ModelAndView();
        if (login.user == null || login.user.isEmpty()) {
            modelAndView.setViewName("login");
            modelAndView.addObject("error", "User name is mandatory");
        } else {
            String userName = login.user;
            response.addCookie(new Cookie("user", userName));
            modelAndView.addObject("user", userName);
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

    @PostMapping("/me")
    public ModelAndView me(@CookieValue(value = "user") String userName, @ModelAttribute Customer customer) {
        ModelAndView modelAndView = new ModelAndView();
        customerRepo.saveCustomer(customer);
        modelAndView.addObject("customer", customer);
        modelAndView.setViewName("me");

/*
        RestTemplate restTemplate = new RestTemplate();
        HackathonLoan loan = HackathonLoan.builder().borrowed(100).customerId("64d4b686-8b65-5ec5-a52b-53a444e69327").build();
        restTemplate.postForEntity("http://psd2loans.automize.org/api/v1/nakamoto/64d4b686-8b65-5ec5-a52b-53a444e69327/loans", loan, HackathonLoan.class);
*/

        return modelAndView;
    }

    @GetMapping("/me")
    public ModelAndView me(@CookieValue(value = "user") String userName) {
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = customerRepo.fetchCustomer(userName).orElse(Customer.builder().id(userName).build());
        modelAndView.addObject("module", "me");
        modelAndView.addObject("customer", customer);
        modelAndView.setViewName("me");
        return modelAndView;
    }

    @GetMapping("/loan")
    public ModelAndView newLoan(@CookieValue(value = "user") String userName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "loan");
        modelAndView.addObject("loanRequest", new LoanRequest());
        modelAndView.setViewName("loan");
        return modelAndView;
    }

    @GetMapping("/loans")
    public ModelAndView loans(@CookieValue(value = "user") String userName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "loans");
        modelAndView.addObject("loanRequests", loanRequestRepo.fetchByCustomerId(userName));
        modelAndView.setViewName("loans");
        return modelAndView;
    }

    @GetMapping("/bank/{bankId}")
    public ModelAndView bankLoans(@CookieValue(value = "user") String userName, @PathVariable("bankId") String bankId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "bank");
        List<BankLoanRequest> bankLoanRequests = bankLoanRequestRepo.fetchByBankId(bankId);
        BankLoanRequest bankLoanRequest = bankLoanRequests.get(0);
        modelAndView.addObject("bankRequest", bankLoanRequest);
        modelAndView.addObject("bankRequests", bankLoanRequestRepo.fetchByBankId(bankId));
        modelAndView.addObject("bankId", bankId);
        modelAndView.setViewName("bank");
        return modelAndView;
    }


    @GetMapping("/bank/{bankId}/{loanId}")
    public ModelAndView bankLoan(@CookieValue(value = "user") String userName, @PathVariable("bankId") String bankId, @PathVariable(value = "loanId", required = false) Long loanId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "bank");
        BankLoanRequest loanRequest = bankLoanRequestRepo.get(loanId).get();
        if (loanId != null && loanId > 0) {
            modelAndView.addObject("bankRequest", bankLoanRequestRepo.get(loanId));
        }
        modelAndView.addObject("bankRequests", bankLoanRequestRepo.fetchByBankId(bankId));
        modelAndView.addObject("bankId", bankId);
        modelAndView.setViewName("bank");
        return modelAndView;
    }

   @PostMapping("/saveBankLoanStatus")
   public ModelAndView saveBankLoanAction(@ModelAttribute BankLoanRequest bankLoanRequest) {
       ModelAndView modelAndView = new ModelAndView();

       log.info("bankLoanRequestStatus({})", bankLoanRequest);

       return modelAndView;
   }

   @GetMapping("/loan/{loanId}")
   public ModelAndView loan(@CookieValue(value = "user") String userName, @PathVariable("loanId") Long loanId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "loan");
        LoanRequest loanRequest = loanRequestRepo.get(loanId).get();
        modelAndView.addObject("loanRequest", loanRequest);
        modelAndView.addObject("bankRequests", bankLoanRequestRepo.fetchByLoanRequestId(loanRequest.getId()));
        modelAndView.setViewName("loan");
        return modelAndView;
    }


    @PostMapping("/loan")
    public ModelAndView createLoan(@CookieValue(value = "user") String userName, @ModelAttribute LoanRequest loanRequest) {
        Customer customer = customerRepo.fetchCustomer(userName).orElseThrow(() -> new RuntimeException("Not logged In"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "loan");
        loanRequest.setCustomer(customer);
        loanRequestRepo.save(loanRequest);
        modelAndView.addObject("loanRequest", loanRequest);
        SendLoanRequestToBanks(loanRequest);
        modelAndView.setViewName("redirect:/loan/" + loanRequest.getId());
        return modelAndView;
    }


    public void SendLoanRequestToBanks(LoanRequest loanRequest) {
        BankLoanRequest bankLoanRequest1 = BankLoanRequest.builder().bankId("ING").build();
        bankLoanRequest1.setLoanRequest(loanRequest);
        BankLoanRequest bankLoanRequest2 = BankLoanRequest.builder().bankId("ABN").build();
        bankLoanRequest2.setLoanRequest(loanRequest);
        BankLoanRequest bankLoanRequest3 = BankLoanRequest.builder().bankId("RABO").build();
        bankLoanRequest3.setLoanRequest(loanRequest);
        bankLoanRequestRepo.save(bankLoanRequest1);
        bankLoanRequestRepo.save(bankLoanRequest2);
        bankLoanRequestRepo.save(bankLoanRequest3);
    }


    @GetMapping("/logout")
    public ModelAndView logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("user", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

}
