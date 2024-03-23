``` mermaid
graph TD
    A((Request<br/>idempotency<br/>key)) --> B{MagDat?}

    B --> |No| C[Customer<br/>message]
    B --> |Yes| D{KanDat?}

    D --> |No| C[Customer<br/>message]
    D --> |Yes| E{Financial checks?}
    
    E --> |No| F[1000 limit]
    E --> |Yes| G[50 limit]

    F --> H[Create card]
    G --> H[Create card]
    H --> T((Inform Customer))

```

