package com.hdfclife.ledger.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hdfclife.ledger.domain.Customer;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.domain.Rider;
import com.hdfclife.ledger.repo.CustomerRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import com.hdfclife.ledger.repo.RiderRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            CustomerRepository customerRepository,
            PolicyRepository policyRepository,
            RiderRepository riderRepository,
            Environment environment) {

        return args -> {

            /*
             * If seeder runs again and policies already exist,
             * do not insert duplicate seed data.
             */
            if (policyRepository.count() > 0) {
                return;
            }

            // -------------------------------------------------
            // 1. Create Customers
            // -------------------------------------------------

            Customer anita = new Customer(
                    "Anita Sharma",
                    "anita.sharma@hdfclife.example"
            );

            Customer rahul = new Customer(
                    "Rahul Mehta",
                    "rahul.mehta@hdfclife.example"
            );

            Customer priya = new Customer(
                    "Priya Nair",
                    "priya.nair@hdfclife.example"
            );

            Customer vikram = new Customer(
                    "Vikram Singh",
                    "vikram.singh@hdfclife.example"
            );

            Customer sneha = new Customer(
                    "Sneha Patel",
                    "sneha.patel@hdfclife.example"
            );

            customerRepository.saveAll(
                    List.of(
                            anita,
                            rahul,
                            priya,
                            vikram,
                            sneha
                    )
            );

            // -------------------------------------------------
            // 2. Create Policies
            // -------------------------------------------------

            Policy policy1001 = new Policy(
                    "HDFC-LIFE-1001",
                    anita,
                    "TERM",
                    18500,
                    "Active"
            );

            Policy policy1002 = new Policy(
                    "HDFC-LIFE-1002",
                    rahul,
                    "ULIP",
                    42000,
                    "Active"
            );

            Policy policy1003 = new Policy(
                    "HDFC-LIFE-1003",
                    priya,
                    "ENDOWMENT",
                    27000,
                    "Lapsed"
            );

            Policy policy1004 = new Policy(
                    "HDFC-LIFE-1004",
                    vikram,
                    "TERM",
                    15200,
                    "Active"
            );

            Policy policy1005 = new Policy(
                    "HDFC-LIFE-1005",
                    sneha,
                    "ULIP",
                    36000,
                    "Active"
            );

            Policy policy1006 = new Policy(
                    "HDFC-LIFE-1006",
                    anita,
                    "ENDOWMENT",
                    22000,
                    "Pending"
            );

            policyRepository.saveAll(
                    List.of(
                            policy1001,
                            policy1002,
                            policy1003,
                            policy1004,
                            policy1005,
                            policy1006
                    )
            );

            // -------------------------------------------------
            // 3. Find Riders inserted by Flyway V2
            // -------------------------------------------------

            Rider accidentCover = riderRepository
                    .findByCode("ACCIDENT_COVER")
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "ACCIDENT_COVER rider not found"
                            ));

            Rider waiverOfPremium = riderRepository
                    .findByCode("WAIVER_OF_PREMIUM")
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "WAIVER_OF_PREMIUM rider not found"
                            ));

            // -------------------------------------------------
            // 4. Attach riders ONLY to HDFC-LIFE-1001
            // -------------------------------------------------

            policy1001.getRiders().add(accidentCover);
            policy1001.getRiders().add(waiverOfPremium);

            policyRepository.save(policy1001);

            // -------------------------------------------------
            // 5. Startup Print
            // -------------------------------------------------

            String activeProfile = environment.getProperty(
                    "spring.profiles.active",
                    "dev"
            );

            long policyCount = policyRepository.count();

            long customerCount = customerRepository.count();

            /*
             * Do NOT call:
             *
             * policy.getCustomer().getFullName()
             *
             * here because Customer is LAZY.
             *
             * Look up the customer directly instead.
             */
            String policy1004Customer = customerRepository
                    .findByEmail("vikram.singh@hdfclife.example")
                    .map(Customer::getFullName)
                    .orElse("Not Found");

            long activeCount = policyRepository
                    .findByStatusOrderByPolicyNoAsc("Active")
                    .size();

            long termCount = policyRepository
                    .findByProductTypeOrderByPolicyNoAsc("TERM")
                    .size();

            long anitaPolicyCount = policyRepository
                    .findByCustomer_FullNameOrderByPolicyNoAsc(
                            "Anita Sharma"
                    )
                    .size();

            String premiumPolicies = policyRepository
                    .findWithPremiumAtLeast(20000)
                    .stream()
                    .map(Policy::getPolicyNo)
                    .reduce(
                            (first, second) ->
                                    first + ", " + second
                    )
                    .orElse("");

            String riderCodes = policy1001
                    .getRiders()
                    .stream()
                    .map(Rider::getCode)
                    .sorted()
                    .reduce(
                            (first, second) ->
                                    first + ", " + second
                    )
                    .orElse("");

            // -------------------------------------------------
            // 6. Print Startup Information
            // -------------------------------------------------

            System.out.println(
                    "Active profile → " + activeProfile
            );

            System.out.println(
                    "Policy count → " + policyCount
            );

            System.out.println(
                    "Customer count → " + customerCount
            );

            System.out.println(
                    "Lookup HDFC-LIFE-1004 customer → "
                            + policy1004Customer
            );

            System.out.println(
                    "Active count → " + activeCount
            );

            System.out.println(
                    "TERM count → " + termCount
            );

            System.out.println(
                    "Anita Sharma policy count → "
                            + anitaPolicyCount
            );

            System.out.println(
                    "minPremium=20000 policy numbers → "
                            + premiumPolicies
            );

            System.out.println(
                    "HDFC-LIFE-1001 rider codes → "
                            + riderCodes
            );
        };
    }
}