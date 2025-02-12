package ng.com.nokt.demodelivery.controllers;

import ng.com.nokt.demodelivery.entites.Item;
import ng.com.nokt.demodelivery.entites.Vehicle;
import ng.com.nokt.demodelivery.services.ItemService;
import ng.com.nokt.demodelivery.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    private final ItemService itemService;
    private final VehicleService vehicleService;

    public VehicleController(ItemService itemService, VehicleService vehicleService) {
        this.itemService = itemService;
        this.vehicleService = vehicleService;
    }

    @PostMapping("/create-vehicle")
    ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle){
        vehicleService.createVehicle(vehicle);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/get-vehicle/{plateNumber}")
    ResponseEntity<Vehicle> getVehicleByPlateNumber(@PathVariable String plateNumber){
        Vehicle vehicle = vehicleService.getVehicleByPlateNumber(plateNumber);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping("add-item-to-vehicle/{plateNumber}/item/{itemId}")
    ResponseEntity<Vehicle> addItemToVehicle(@PathVariable String plateNumber, @PathVariable Long itemId){
        Item item = itemService.getItemById(itemId);
        Vehicle vehicle = vehicleService.getVehicleByPlateNumber(plateNumber);
        List<Item> items = vehicle.getItems();
        float weightOnVehicle = 0;

        for(Item listItem: items){
            weightOnVehicle += listItem.getWeight();
        }

        if ((weightOnVehicle + item.getWeight()) < vehicle.getCarryingWeight()){
            vehicle.getItems().add(item);
            vehicleService.createVehicle(vehicle);
            return ResponseEntity.ok(vehicle);
        }else {
            throw new RuntimeException("Too much weight to this vehicle");
        }

    }
    @DeleteMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("message", "Vehicle deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting vehicle: " + e.getMessage());
        }
        return "redirect:/create-vehicle";
    } 
}
