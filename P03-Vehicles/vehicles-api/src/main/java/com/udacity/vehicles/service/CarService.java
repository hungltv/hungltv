package com.udacity.vehicles.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.udacity.vehicles.VehiclesApiApplication;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private WebClient mapsClient;
    private WebClient pricingClient;
    private static final Logger log = LoggerFactory.getLogger(PriceClient.class);
	InputStream inputStream;

    public CarService(CarRepository repository) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
    	String mapsEndpoint = "";
    	String pricingEndpoint = "";
    	try {
    		Properties prop = new Properties();
    		String propFileName = "application.properties";
    		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
	            log.warn("inputStream is null");
			}
			mapsEndpoint = prop.getProperty("maps.endpoint");
			pricingEndpoint = prop.getProperty("pricing.endpoint");

		} catch (Exception e) {
            log.error("Not found \"application.properties\" file");

		}
		
    	VehiclesApiApplication app = new VehiclesApiApplication();
    	mapsClient = app.webClientMaps(mapsEndpoint);
    	pricingClient = app.webClientPricing(pricingEndpoint);
        this.repository = repository;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
        Car car = new Car();
        Optional<Car> optionalCar = Optional.of(new Car());
        optionalCar = repository.findById(id);
        if(!optionalCar.isPresent()) {
        	throw new CarNotFoundException("Not found car id " + id);
        }
        Car carInfo = optionalCar.get();
        Condition condition = carInfo.getCondition();
    	;
        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        PriceClient pricingCl = new PriceClient(pricingClient);
        String[] priceOfCar = pricingCl.getPrice(id).split(" ");
        String price = "0";
        try {
			Double pricedb = Double.valueOf(priceOfCar[1]);
	        price = pricedb.toString();
		} catch (Exception e) {
            log.error("Not found price for vehicle", id, e);
		}
        car.setPrice(price);
        car.setId(id);
        car.setCreatedAt(carInfo.getCreatedAt());
        car.setModifiedAt(carInfo.getCreatedAt());
        car.setCondition(condition);
        car.setDetails(carInfo.getDetails());
        
        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        MapsClient mapsCl = new MapsClient(mapsClient, new ModelMapper());
        
        Location location = car.getLocation();
        location = mapsCl.getAddress(location);
        
        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Optional<Car> optionalCar = Optional.of(new Car());
        optionalCar = repository.findById(id);
        
        /**
         * TODO: Delete the car from the repository.
         */
        if(optionalCar.isPresent()) {
        	repository.deleteById(id);
        } else {
        	throw new CarNotFoundException();
        }

    }
}
