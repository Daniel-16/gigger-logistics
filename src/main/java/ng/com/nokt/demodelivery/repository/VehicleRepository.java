package ng.com.nokt.demodelivery.repository;

import ng.com.nokt.demodelivery.entites.Item;
import ng.com.nokt.demodelivery.entites.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT vehicle FROM Vehicle vehicle WHERE vehicle.status='maintenance'")
    List<Vehicle> getVehiclesStatus();
    List<Vehicle> getVehicleByCarryingWeightGreaterThan(float weight);
    List<Vehicle> findByItemsContaining(Item item);

    Vehicle getVehicleByPlateNumber(String plateNumber);
}
