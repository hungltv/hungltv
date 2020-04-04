package com.example.pricinggService.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.example.pricinggService.model.Price;
import com.exemple.pricinggService.exception.PriceException;

public class PricingService {
	public PricingService() {
	}
	 /**
     * Holds {ID: Price} pairings (current implementation allows for 20 vehicles)
     */
	static LongStream stream = LongStream.range(1, 20);
	static Stream<Price> streamPice = stream.mapToObj(i -> new Price("USD", randomPrice(), i));
	static Map<Long, Price> mapLongPrice = streamPice.collect(Collectors.toMap(Price::getVehicleId, p -> p));
    
	private static final Map<Long, Price> PRICES = mapLongPrice;

    /**
     * If a valid vehicle ID, gets the price of the vehicle from the stored array.
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     * @throws PriceException vehicleID was not found
     */
    public static Price getPrice(Long vehicleId) throws PriceException {

        if (!PRICES.containsKey(vehicleId)) {
            throw new PriceException("Cannot find price for Vehicle " + vehicleId);
        }

        return PRICES.get(vehicleId);
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }
}
