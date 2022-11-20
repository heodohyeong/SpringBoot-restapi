package me.whiteship.demoinfleanrestapi.common;

import me.whiteship.demoinfleanrestapi.events.Event;
import me.whiteship.demoinfleanrestapi.events.EventController;
import me.whiteship.demoinfleanrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.Link.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource  extends EntityModel<Errors>{
    /*public ErrorsResource(Errors content, Link... links) {
        super(content,  Arrays.asList(links));
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }*/
   /* public EntityModel<Errors> addLink(Errors content) {
        EntityModel<Errors> entityModel = EntityModel.of(content);
        entityModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return entityModel;
    }*/
    /*public static EntityModel<Errors> modelOf(Errors errors) {
        EntityModel<Errors> errorsModel = EntityModel.of(errors);
        //errorsModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        //System.out.println(errorsModel);
        return errorsModel;
    }*/

    public ErrorsResource(Errors errors, Iterable<Link> links) {
        super(errors, links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

    /*public ErrorsResource(Errors errors) {
        List<Link> links = Arrays.asList();
        new ErrorsResource(errors, links);
    }*/


}
