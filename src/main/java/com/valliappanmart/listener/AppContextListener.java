package com.valliappanmart.listener;

import com.valliappanmart.config.DatabaseInitializer;
import com.valliappanmart.dao.CategoryDao;
import com.valliappanmart.model.Category;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.List;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Valliappan Mart Application starting up...");
        DatabaseInitializer.initialize();

        CategoryDao categoryDao = new CategoryDao();
        List<Category> categories = categoryDao.findAll();
        sce.getServletContext().setAttribute("globalCategories", categories);
        LOGGER.info("Valliappan Mart Application initialized with " + categories.size() + " categories.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Valliappan Mart Application shut down.");
    }
}
