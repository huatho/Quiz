package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Product;
import com.example.demo.repo.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(Long id) {
		Optional<Product> optional = productRepository.findById(id);
		return optional.orElse(null);
	}

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	public Product updateProduct(Long id, Product newProduct) {
		Product oldProduct = getProductById(id);
		if (oldProduct == null) {
			return null;
		}

		oldProduct.setName(newProduct.getName());
		oldProduct.setPrice(newProduct.getPrice());
		oldProduct.setQuantity(newProduct.getQuantity());

		return productRepository.save(oldProduct);
	}

	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
}
