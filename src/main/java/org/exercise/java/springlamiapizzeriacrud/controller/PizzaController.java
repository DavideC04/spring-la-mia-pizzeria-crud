package org.exercise.java.springlamiapizzeriacrud.controller;

import jakarta.validation.Valid;
import org.exercise.java.springlamiapizzeriacrud.model.Pizza;
import org.exercise.java.springlamiapizzeriacrud.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pizza")
public class PizzaController {

    @Autowired
    private PizzaRepository pizzaRepository;

    @GetMapping
    public String index(Model model) {
        List<Pizza> pizzaList = pizzaRepository.findAll();
        model.addAttribute("pizza", pizzaList);
        return "pizze/list";
    }

    // metodo show che mostra i dettagli di una pizza preso per id
    @GetMapping("/show/{pizzaId}")
    public String show(@PathVariable("pizzaId") Integer id, Model model) {
        // uso l'id preso dal path della requestMapping per cercare quella pizza nel database
        // chiedo al pizzaRepository di fare un findById che può o non può ritornare una Pizza
        Optional<Pizza> pizzaOptional = pizzaRepository.findById(id);
        // se nell'Optional c'è la pizza, proseguo e passo alla view
        if (pizzaOptional.isPresent()) {
            Pizza pizzaDB = pizzaOptional.get(); //chiedo all'optional di mostrare il suo contenuto
            model.addAttribute("pizze", pizzaDB);
            return "pizze/detail";
        } else {
            // se l'Optiona è vuoto, sollevo un'eccezione
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }


    @GetMapping("/create")
    public String create(Model model) {
        // aggiungo al model un attributo di tipo Pizza
        model.addAttribute("pizze", new Pizza());
        return "pizze/form";
    }


    @PostMapping("/create")
    public String doCreate(@Valid @ModelAttribute("pizze") Pizza formPizza, BindingResult bindingResult) {
        // se ci sono errori di validazioni, ritorno al template del form
        if (bindingResult.hasErrors()) {
            return "pizze/form";
        }
        // per salvare la pizza sul database, richiamo il pizzaRepository
        pizzaRepository.save(formPizza);
        // se la pizza è stata salvata,faccio una redirect alla lista delle pizze
        return "redirect:/pizza";
    }


    // metodo per effettuare l'update dei dati delle pizze

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        // cerco sul database la pizza con quell'id
        Optional<Pizza> result = pizzaRepository.findById(id);
        // verifico se la pizza è presente
        if (result.isPresent()) {
            // passo la Pizza come model dell'attributo
            model.addAttribute("pizze", result.get());
            // ritorno il template con l'edit
            return "pizze/update";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza with id " + id + " not found");
        }

    }

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable Integer id, @Valid @ModelAttribute("pizze") Pizza formPizza, BindingResult bindingResult) {
        // valido i dati
        if (bindingResult.hasErrors()) {
            // si sono verificati degli errori di validazione
            return "pizze/update";
        }
        // salvo la Pizza attraverso il pizzaRepository
        pizzaRepository.save(formPizza);
        return "redirect:/pizza";

    }

    // metodo per effettuare la cancellazione dei dati delle pizze
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        // cancello la pizza attraverso il pizzaRepository
        pizzaRepository.deleteById(id);
        // una volta cancellato, reindirizzo alla lista di pizze
        return "redirect:/pizza";
    }
}
