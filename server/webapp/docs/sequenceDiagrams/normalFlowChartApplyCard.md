``` mermaid
graph TD
    A((Request<br/>iban)) --> B{MagDat?}

    B --> |No| C((End))
    B --> |Yes| D{KanDat?}

    D --> |No| E((End))
    D --> |Yes| F{Financial<br/>checks?}

    F --> |No| G((End))
    F --> |Yes| H[Authorize]
    
    H --> I{All checks?}
    
    I --> |No| J((END))
    I --> |Yes| K[Create card]
    
    K --> T((Inform Customer))

```

