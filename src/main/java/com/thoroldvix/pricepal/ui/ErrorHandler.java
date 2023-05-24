package com.thoroldvix.pricepal.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import jakarta.servlet.http.HttpServletResponse;


public abstract class ErrorHandler<T extends RuntimeException> extends Div implements HasErrorParameter<T> {

    protected Paragraph message = new Paragraph();

    public ErrorHandler() {
        setClassName("custom-error-page");
        setSizeFull();

        H1 title = new H1("Oops!");
        title.addClassName("error-title");


        message.addClassName("error-message");

        add(title, message);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<T>
                                         parameter) {

        if (message != null) {
            message.setText(parameter.getCustomMessage());
        }

        return HttpServletResponse.SC_NOT_FOUND;
    }
}
