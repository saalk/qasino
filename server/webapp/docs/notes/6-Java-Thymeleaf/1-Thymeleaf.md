Back to [Index](0-index.md)
## Thymeleaf
Thymeleaf is a Java template engine for processing and creating HTML, XML, JavaScript, CSS and text.

## Integrating Thymeleaf with Spring
Thymeleaf offers a set of Spring integrations that allow you to use it as a fully-featured substitute for JSP in Spring MVC applications.

1. Mapped methods in @Controller forward objects to templates managed by Thymeleaf like you do with JSPs
2. Use Spring Expression Language (Spring EL) instead of OGNL in your templates
3. Create forms in your templates that are 
   - integrated with form-backing beans 
   - and result bindings, 
   - including the use of property editors, conversion services and validation error handling
4. Display internationalization messages from message files managed by Spring
5. Resolve your templates using Spring’s own resource resolution mechanisms

## Standard dialect
1. SPEL ${...} and *{...} expressions
2. Access any beans in your application context using SpringEL’s syntax: ${@myBean.doSomething()}
3. Form processing
   - **th:field** - use the th:field attribute on a form input to instruct Thymeleaf to add field-specific attributes to the form, such as name and id
   - **th:errors** -
   - **th:errorclass**
   - **th:object**
4. An expression object and method, #themes.code(...)
5. An expression object and method, #mvc.uri(...)

## 1. Mapped methods
Typically, controllers ask ViewResolvers to forward to a view with a specific name 
(a String returned by the controller method), and then all the view resolvers in the application 
execute in ordered chain until one of them is able to resolve that view, in which case a View object 
is returned and control is passed to it for the renderization of HTML.

```java
@GetMapping({"/visitor"}) // showing the form page
public String displayCreateVisitorForm(
        Model model) {
  model.addAttribute("title", "Create Visitor");
  model.addAttribute("event", new Visitor());

  // model.addAttribute("new Event());
  // here Spring will implicitly create the label "event",
  // which is the lowercase version of the class name
   
  return "page/visitor";
}

// with a request only mapped if each such parameter is found
@PostMapping(value="/visitor", params={"save"})
// RequestMapping(value = "/visitor", method = RequestMethod.POST)
// RequestMapping("/visitor", method = RequestMethod.POST)
public String processCreateVisitorForm(
        @ModelAttribute @Validated(BasicInfo.class) Visitor newVisitor, 
        BindingResult bindingResult,
        Errors errors, // 
        final ModelMap model) {
    
    if (bindingResult.hasErrors()) {
        return "visitor";
    }
    if (errors.hasErrors()) {  // 
        return "visitor";
    }
    this.visitorService.add(newVisitor);
    model.clear();
    return "redirect:/visitor";
}
```
**@Valid** - a spring based annotation does method level and member attribute validation.

This method checks for validation errors and returns the visitor to the form if it finds any. 
It uses model binding to create a new event object, but this event object is also passed into 
the view when re-rendering the form. 

This means that if there are validation errors, the form will be rendered with the values that the user previously entered, 
preventing the user from having to re-enter all of their data.

## 2. SPEL
SpEL stands for Spring Expression Language and is a powerful tool that can significantly enhance our interaction with Spring and provide an additional abstraction over configuration, property settings, and query manipulation.

SpEL expressions begin with the # symbol and are wrapped in braces: **#{expression}**.
Properties can be referenced in a similar fashion, starting with a $ symbol and wrapped in braces: **${property.name}**.
> #{${someProperty} + 2} // assume someProperty has value 2 then result is 4
```java
@Value("#{37 % 10}") // 7
private double modulo;

@Value("#{250 > 200 && 200 < 4000}") // true
private boolean and;

@Value("#{2 > 1 ? 'a' : 'b'}") // "a"
private String ternary;

@Value("#{someBean.someProperty != null ? someBean.someProperty : 'default'}")
private String ternary;

@Value("#{carPark.cars[0]}") // Model1
private Car firstCarInPark;
```

## HTML Forms

The **\<form\>** element is a container for different types of input elements, such as: text fields, checkboxes, radio buttons, submit buttons, etc.

The **\<label\>** element also helps users who have difficulty clicking on very small regions (such as radio buttons or checkboxes) - because when the user clicks the text within the <label> element, it toggles the radio button/checkbox.

The for attribute of the **\<label\>** tag should be equal to the id attribute of the **\<input\>** element to bind them together.

The **\<input type="submit"\>** defines a button for submitting the form data to a form-handler.
The form-handler is typically a file on the server with a script for processing input data.
The form-handler is specified in the form's **action** attribute.
```html
<form action="/action_page.php" method="post">
   <label for="fname">First name:</label><br>
   <input type="text" id="fname" name="fname"><br>
   
   <input type="submit" value="Submit">
</form>

<input type="text"> Displays a single-line text input field
<input type="radio"> Displays a radio button (for selecting one of many choices)
<input type="checkbox">	Displays a checkbox (for selecting zero or more of many choices)
<input type="submit"> Displays a submit button (for submitting the form)
<input type="button"> Displays a clickable button
```
Notice that each input field must have a name attribute to be submitted.
If the name attribute is omitted, the value of the input field will not be sent at all.

## 3. Form thymeleaf example

#### th:field
```html
<label>Description
   <input type="text" th:field="${event.description}" class="form-control" />
</label>
```
```java
> th:field="${event.contactEmail}"
> will become => id="contactEmail" name="contactEmail" value="me@me.com"
```
Class Event
```java
public class Event {
    
    @NotBlank(message = "Name is required", groups = BasicInfo.class)
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Size(max = 500, message = "Description too long!")
    private String description;

    @NotBlank(message = "Email is required", groups = AdvanceInfo.class)
    @Email(message = "Invalid email. Try again.")
    private String contactEmail;

   @Valid // ensure validation of this nested object
   @NotNull(groups = AdvanceInfo.class)
   private UserAddress useraddress;
}

@GetMapping("create")
public String displayCreateEventForm(Model model) {
   model.addAttribute("title", "Create Event");
   model.addAttribute("event", new Event());
   
   // model.addAttribute("new Event());
   // here Spring will implicitly create the label "event",
   // which is the lowercase version of the class name
      
   return "events/create";
}
```

#### th:errors
```html
<form method="post">
   <div class="form-group">
      <label>Name
         <input type="text" th:field="${event.name}" class="form-control" />
      </label>
      <p class="error" th:errors="${event.name}"></p>
   </div>
   ...
   <div class="form-group">
      <input type="submit" value="Create" class="btn btn-success" />
   </div>
</form>
```
Setting **th:errors="${event.name}"** tells Thymeleaf to insert any error messages related to the name field of event into the paragraph element. We add class="error" to allow us to style this element, for example with red text. A simple rule in our styles.css file will do the trick:
```css
.error {
color: red;
}
```
#### th:object
Command object is the name Spring MVC gives to form-backing beans, this is, to objects that model a form’s fields and provide getter and setter methods that will be used by the framework for establishing and obtaining the values input by the user at the browser side.

Thymeleaf requires you to specify the command object by using a th:object attribute in your <form> tag:
```html
<form action="#" th:action="@{/seedstartermng}" th:object="${seedStarter}" method="post">
   ...
</form>
```
- Values for th:object attributes in form tags must be variable expressions (${...}) specifying only the name of a model attribute, without property navigation. 
- This means that an expression like ${seedStarter} is valid, but ${seedStarter.data} would not be.
- Once inside the \<form\> tag, no other th:object attribute can be specified. 
- This is consistent with the fact that HTML forms cannot be nested.
```html
<<ul>
   <li th:each="ty : ${allTypes}">
      <input type="radio" th:field="*{type}" th:value="${ty}" />
      <label th:for="${#ids.prev('type')}" th:text="#{${'seedstarter.type.' + ty}}">Wireframe</label>
   </li>
</ul>
```
We can see here how a sequence suffix is added to each input’s id attribute, and how the #ids.prev(...) function allows us to retrieve the last sequence value generated for a specific input id.

#### th:errorclass
Applied to a form field tag (input, select, textarea…), it will read the name of the field to be examined from any existing name or th:field attributes in the same tag, and then append the specified CSS class to the tag if such field has any associated errors:
```html
<input type="text" th:field="*{datePlanted}" class="small" th:errorclass="fieldError" />
// If datePlanted has errors, this will render as:
<input type="text" id="datePlanted" name="datePlanted" value="2013-01-01" class="small fieldError" />
```




