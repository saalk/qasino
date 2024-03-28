``` mermaid
graph TD
    A((Request<br/>iban)) --> B{MagDat?}

    B --> |No| C{Check <br/>overrule?}
    B --> |Yes| D{KanDat?}
    
    C --> |No| E((End))
    C --> |Yes| D{KanDat?}

    D --> |No| F{Check<br/> overrule?}
    D --> |Yes| G{Financial<br/>checks}

    F --> |No| H((End))
    F --> |Yes| G{Financial<br/>checks?}
    
    G --> |No| I{Check <br/>overrule?}
    G --> |Yes| J[Authorize]
    
    I --> |No| K((End))
    I --> |Yes| J[Authorize]
    
    J --> L{All checks?}
    
    L --> |No| M{Check<br/> overrule?}
    L --> |Yes| N[Create card]
    
    M --> |No| P((End))
    M --> |Yes| N[Create card]
    
    N --> Q{disabled<br/>customer<br/>message?}
    
    Q --> |No| R[Inform customer]
    Q --> |Yes| S((End))

```

