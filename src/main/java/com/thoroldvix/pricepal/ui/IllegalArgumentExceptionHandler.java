package com.thoroldvix.pricepal.ui;

import com.vaadin.flow.router.Route;

@Route(value = "illegal-argument", layout = MainLayout.class)
public class IllegalArgumentExceptionHandler extends ErrorHandler<IllegalArgumentException> {

}

