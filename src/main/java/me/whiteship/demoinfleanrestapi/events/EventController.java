package me.whiteship.demoinfleanrestapi.events;


import me.whiteship.demoinfleanrestapi.accounts.Account;
import me.whiteship.demoinfleanrestapi.accounts.AccountAdaptor;
import me.whiteship.demoinfleanrestapi.accounts.CurrentUser;
import me.whiteship.demoinfleanrestapi.common.ErrorsResource;
import me.whiteship.demoinfleanrestapi.index.IndexController;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.Link.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value="/api/events" , produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;


    public EventController(EventRepository eventRepository , ModelMapper modelMapper , EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto ,
                                      Errors errors,
                                      @CurrentUser Account account) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto , errors);

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto , Event.class);
        event.update();
        event.setManager(account);
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        List<Link> links = Arrays.asList(
              //  selfLinkBuilder.slash(newEvent.getId()).withSelfRel(),
                selfLinkBuilder.withRel("query-events"),
                selfLinkBuilder.withRel("update-event"),
                of("/docs/index.html#resources-events-create").withRel("profile")
        );

        EventResource eventResource = new EventResource(event , links);
        //eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //eventResource.add(selfLinkBuilder.withSelfRel());
        //eventResource.add(selfLinkBuilder.withRel("update-event"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable ,
                                     PagedResourcesAssembler<Event> assembler,
                                     @CurrentUser Account account){
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //User user = (User) authentication.getPrincipal();

        Page<Event> page = this.eventRepository.findAll(pageable);
        List<Link> links = Arrays.asList();
        var pagedResources = assembler.toModel(page , e -> new EventResource(e , links));
        pagedResources.add(of("/docs/index.html#resources-events-list").withRel("profile"));
        if(account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account account){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        List<Link> links = Arrays.asList(
                of("/docs/index.html#resources-events-get").withRel("profile")
        );
        EventResource eventResource = new EventResource(event, links);

        if(event.getManager().equals(account)){
            links.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id , @RequestBody @Valid EventDto eventDto
            , Errors errors
    ,@CurrentUser Account account) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto , errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();

        if(!existingEvent.getManager().equals(account)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto , existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);



        List<Link> links = Arrays.asList(
                of("/docs/index.html#resources-events-update").withRel("profile")
        );
        EventResource eventResource = new EventResource(savedEvent , links);
        return ResponseEntity.ok(eventResource);
    }

    public ResponseEntity badRequest(Errors errors){
        //return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
        List<Link> links = Arrays.asList();
        //ErrorsResource errorsResource = new ErrorsResource(errors);
        return ResponseEntity.badRequest().body(new ErrorsResource(errors , links));
    }
}
