package ng.com.nokt.demodelivery.services;

import ng.com.nokt.demodelivery.entites.Item;
import ng.com.nokt.demodelivery.entites.Vehicle;
import ng.com.nokt.demodelivery.repository.ItemRepository;
import ng.com.nokt.demodelivery.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final VehicleRepository vehicleRepository;

    public ItemServiceImpl(ItemRepository itemRepository, VehicleRepository vehicleRepository) {
        this.itemRepository = itemRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Item createItem(Item item) {
        item.setCode(UUID.randomUUID().toString());
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).get();
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        List<Vehicle> vehicles = vehicleRepository.findByItemsContaining(item);
        for (Vehicle vehicle : vehicles) {
            vehicle.getItems().remove(item);
            vehicleRepository.save(vehicle);
        }        
        itemRepository.deleteById(id);
    }
}
