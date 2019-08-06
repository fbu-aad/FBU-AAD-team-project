package com.example.una.models;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DonationTest {

    @Test
    public void test_getTimestamp_test() {
        Date date = new GregorianCalendar(2014, 02, 11).getTime();
        Timestamp time = new Timestamp(date);
        Map<String, Object> donationTests = new HashMap<>();
        donationTests.put("time", time);

        Donation donation = new Donation(donationTests);

        Timestamp outputTime = donation.getTimestamp();

        Assert.assertEquals(time, outputTime);
    }

    @Test
    public void test_getDonationAmount_test() {
        Date date = new GregorianCalendar(2014, 02, 11).getTime();
        Timestamp time = new Timestamp(date);
        Map<String, Object> donationTests = new HashMap<>();
        donationTests.put("amount", 123456);

        Donation donation = new Donation(donationTests);

        Number outputAmount = donation.getDonationAmount();

        Assert.assertEquals(123456, outputAmount);
    }

    @Test
    public void test_getRecipient_test() {
        Map<String, Object> donationTests = new HashMap<>();
        donationTests.put("recipient", "Patric");

        Donation donation = new Donation(donationTests);

        String outputRecipient = donation.getRecipient();

        Assert.assertEquals("Patric", outputRecipient);
    }

    @Test
    public void test_getDonorId_test() {
        Map<String, Object> donationTests = new HashMap<>();
        donationTests.put("donor_id", "0325694");

        Donation donation = new Donation(donationTests);

        String outputDonorID = donation.getDonorId();

        Assert.assertEquals("0325694", outputDonorID);
    }

    @Test
    public void test_getAllAccessorsWithNoValues_test() {
        Map<String, Object> donationTests = new HashMap<>();

        Donation donation = new Donation(donationTests);

        Number outputAmount = donation.getDonationAmount();
        Timestamp outputTime = donation.getTimestamp();
        String outputRecipient = donation.getRecipient();
        String outputDonorId = donation.getDonorId();

        Assert.assertNull(outputAmount);
        Assert.assertNull(outputTime);
        Assert.assertNull(outputRecipient);
        Assert.assertNull(outputDonorId);
    }

    @Test
    public void test_getAllAccessorsWithPresetValues_test() {
        Date date = new GregorianCalendar(2014, 02, 11).getTime();
        Timestamp timestamp = new Timestamp(date);
        Map<String, Object> donationTests = new HashMap<>();
        donationTests.put("amount", 123456);
        donationTests.put("time",timestamp);
        donationTests.put("recipient","Denize");
        donationTests.put("donor_id","987654321");
        Donation donation = new Donation(donationTests);

        Number outputAmount = donation.getDonationAmount();
        Timestamp outputTime = donation.getTimestamp();
        String outputRecipient = donation.getRecipient();
        String outputDonorId = donation.getDonorId();

        Assert.assertEquals(123456, outputAmount);
        Assert.assertEquals(timestamp, outputTime);
        Assert.assertEquals("Denize", outputRecipient);
        Assert.assertEquals("987654321", outputDonorId);
    }
}