package com.thoroldvix.g2gcalculator.ui;

import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;

@Route(value = "not-found", layout = MainLayout.class)
public class NotFoundExceptionHandler extends ErrorHandler<NotFoundException> {
}

