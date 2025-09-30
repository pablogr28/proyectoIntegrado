package com.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.Category;
import com.e_commerce.repository.CategoryRepository;

@Service
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository=categoryRepository;
	}
	
	public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category
        		);
    }

    public void deleteCategory(Long id) {
    	categoryRepository.deleteById(id);
    }

}
