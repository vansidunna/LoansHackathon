package com.example.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

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

    @PostMapping("/loan")
    public ModelAndView createLoan(@CookieValue(value = "user") String userName, @ModelAttribute LoanRequest loanRequest) {
        Customer customer = customerRepo.fetchCustomer(userName).orElseThrow(() -> new RuntimeException("Not logged In"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("module", "loan");
        loanRequest.setCustomer(customer);
        loanRequestRepo.save(loanRequest);
        modelAndView.addObject("loanRequest", loanRequest);
        SendLoanRequestToBanks(loanRequest);
        modelAndView.setViewName("loan");
        return modelAndView;
    }

    @PostMapping("/banks")
    public ModelAndView SendLoanRequestToBanks(@ModelAttribute LoanRequest loanRequest) {
        ModelAndView modelAndView = new ModelAndView();
        BankLoanRequest bankLoanRequest1 = BankLoanRequest.builder().bankId("ING").build();
        bankLoanRequest1.setLoanRequest(loanRequest);
        BankLoanRequest bankLoanRequest2 = BankLoanRequest.builder().bankId("ABN").build();
        bankLoanRequest2.setLoanRequest(loanRequest);
        BankLoanRequest bankLoanRequest3 = BankLoanRequest.builder().bankId("RABO").build();
        bankLoanRequest3.setLoanRequest(loanRequest);
        bankLoanRequestRepo.save(bankLoanRequest1);
        bankLoanRequestRepo.save(bankLoanRequest2);
        bankLoanRequestRepo.save(bankLoanRequest3);
        modelAndView.addObject("bankLoanRequest1", bankLoanRequest1);
        modelAndView.addObject("bankLoanRequest2", bankLoanRequest2);
        modelAndView.addObject("bankLoanRequest3", bankLoanRequest3);
        return modelAndView;
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
