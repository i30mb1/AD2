`@Inject` - request dependencies, can be used on a constructor, a field, a method
`@Component` - enable selected modules and used for performing depedency injection
`@Module` - used on classes which contains mehtod annotated

- `@Providers` - tells how to provide classes that your project does not own
- `@Bind` - do the same but using with interfaces and less code
  `@Singleton` - generate single instance
  `@Subcomponent` - организовавывае связть между компонентами
  `@Qualifier` - помогает разрешить мультибайндинг
  `Lazy<Something>` - lazy computed value
  `Provider<Something>` - lazy computed value, generate new object on every call
