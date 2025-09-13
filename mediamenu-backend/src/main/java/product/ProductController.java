package product;

import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @PostMapping
    public String createProduct() {
        return "Product Created";
    }

    @GetMapping
    public String getProduct() {
        return "Got product";
    }


    @PutMapping
    public String updateProduct() {
        return "Updated Product";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "Product Deleted";
    }


}
