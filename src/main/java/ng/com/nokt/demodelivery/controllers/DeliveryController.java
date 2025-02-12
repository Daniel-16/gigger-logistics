package ng.com.nokt.demodelivery.controllers;

import ng.com.nokt.demodelivery.entites.Item;
import ng.com.nokt.demodelivery.entites.Vehicle;
import ng.com.nokt.demodelivery.services.ItemService;
import ng.com.nokt.demodelivery.services.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DeliveryController {

    private final VehicleService vehicleService;
    private final ItemService itemService;

    public DeliveryController(VehicleService vehicleService, ItemService itemService) {
        this.vehicleService = vehicleService;
        this.itemService = itemService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @GetMapping("/create-vehicle")
    public String createVehicle(Model model){
        Vehicle vehicle = new Vehicle();
        model.addAttribute("vehicle", vehicle)
                .addAttribute("allVehicles", vehicleService.getAllVehicles());
        return "feature";
    }

    @PostMapping("/post-vehicle")
    public String postVehicle(Model model, @ModelAttribute("vehicle") Vehicle vehicle){
        String message = "Vehicle created successfully";
        vehicleService.createVehicle(vehicle);
        model.addAttribute("message", message);
        model.addAttribute("vehicle", vehicle).addAttribute("allVehicles", vehicleService.getAllVehicles());
        return "redirect:/create-vehicle";
    }

    @GetMapping("/create-item")
    public String createItemForm(Model model) {
        model.addAttribute("allItems", itemService.getAllItems());
        return "create-item";
}

    @PostMapping("/post-item")
    public String postItem(Model model, @ModelAttribute("item") Item item) {
        itemService.createItem(item);
        model.addAttribute("item", item).addAttribute("allItems", itemService.getAllItems());
        return "redirect:/create-item";
}

    @GetMapping("/assign-item")
    public String showAssignItemForm(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("items", itemService.getAllItems());
        model.addAttribute("assignmentForm", new ItemAssignment());
        return "assign-item";
    }

    @PostMapping("/assign-item")
    public String assignItem(@ModelAttribute("assignmentForm") ItemAssignment form, 
                           RedirectAttributes redirectAttributes) {
        try {
            // Get vehicle and item
            Vehicle vehicle = vehicleService.getVehicleByPlateNumber(form.getPlateNumber());
            Item item = itemService.getItemById(form.getItemId());

            // Calculate total current weight
            float currentWeight = 0;
            if (vehicle.getItems() != null) {
                currentWeight = vehicle.getItems().stream()
                    .map(Item::getWeight)
                    .reduce(0f, Float::sum);
            }

            // Check if adding new item exceeds capacity
            if (currentWeight + item.getWeight() > vehicle.getCarryingWeight()) {
                redirectAttributes.addFlashAttribute("error", 
                    String.format("Cannot assign item. Total weight (%.2f kg) would exceed vehicle capacity of %.2f kg", 
                        currentWeight + item.getWeight(), vehicle.getCarryingWeight()));
                return "redirect:/assign-item";
            }

            // Add item to vehicle
            vehicle.getItems().add(item);
            vehicleService.createVehicle(vehicle);

            redirectAttributes.addFlashAttribute("success", 
                String.format("Item '%s' successfully assigned to vehicle '%s'. Remaining capacity: %.2f kg", 
                    item.getName(), vehicle.getName(), 
                    vehicle.getCarryingWeight() - (currentWeight + item.getWeight())));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error assigning item: " + e.getMessage());
        }
        
        return "redirect:/assign-item";
    }

}
