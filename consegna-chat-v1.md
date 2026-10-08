# Multichat — V1

Realizza una web app in cui gli utenti possono registrarsi, accedere e partecipare a tre chat tematiche, leggendo tutta la cronologia e ricevendo i nuovi messaggi in tempo reale. Usa Spring Boot e MySQL in due container Docker separati, Angular sul PC e VSCode come IDE. Le chat sono fisse: **Generale**, **Tecnologia** e **Tempo libero**; la loro gestione da parte degli amministratori verrà aggiunta in una versione successiva.

## Task 1 — Creare il progetto

Apri un terminale PowerShell nella cartella in cui vuoi lavorare ed esegui:

```powershell
mkdir multichat
cd multichat
code .
```

Crea il backend direttamente in VSCode con l'estensione **Spring Initializr Java Support**, già presente nell'ambiente:

1. Apri la palette dei comandi con **Ctrl+Shift+P**.
2. Cerca e seleziona **Spring Initializr: Create a Maven Project**.
3. Segui la procedura guidata scegliendo la versione stabile di Spring Boot, Java, Group Id, Artifact Id, packaging, versione Java e dipendenze indicate sotto.

| Impostazione | Valore |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | Una versione stabile, senza suffissi SNAPSHOT, M o RC |
| Group | `com.progettiportfoglio` |
| Artifact | `multichat` |
| Name | `multichat` |
| Package name | `com.progettiportfoglio.multichat` |
| Packaging | Jar |
| Java | 21 |
| Dipendenze | Spring Web, Spring Security, Spring Data JPA, Validation, WebSocket, MySQL Driver |

Al termine, scegli la root `multichat/` come destinazione e rinomina in `backend` la sottocartella del progetto generato. Il POM deve trovarsi in `multichat/backend/pom.xml`. Cambia soltanto il nome della cartella: l'Artifact Id e il nome del progetto restano `multichat`. Mantieni aperta in VSCode la root, che conterrà anche Angular.

Il nome e il package possono essere derivati automaticamente dall'estensione a partire da Group Id e Artifact Id: usa `multichat` e `com.progettiportfoglio.multichat`. Riferimento: [creazione di progetti Spring Boot in VSCode](https://code.visualstudio.com/docs/java/java-spring-boot).

Nel `pom.xml`, aggiungi `<finalName>app</finalName>` dentro la sezione `<build>` già presente. Mantieni il plugin Spring Boot generato: il Dockerfile userà il file `target/app.jar`.

Per il frontend usa **Angular 22** e la relativa CLI già installata. Dalla root `multichat/`, genera il progetto:

```powershell
ng.cmd new multichat --directory=frontend --routing --style=scss --standalone --strict --ssr=false --skip-git --skip-tests --package-manager=npm --defaults
cd frontend
npm.cmd install @stomp/stompjs
cd ..
```

Copia ora **il contenuto** della cartella allegata [basi-progetto](basi-progetto/LEGGIMI.md) nella root `multichat/`, unendo le cartelle esistenti. I file Java devono rimanere nel package `com.progettiportfoglio.multichat` e nei suoi sottopackage.

La struttura di lavoro sarà:

```text
multichat/
├── backend/
│   ├── src/main/java/com/progettiportfoglio/multichat/
│   │   ├── auth/
│   │   ├── config/
│   │   ├── security/
│   │   ├── user/
│   │   ├── chat/
│   │   ├── message/
│   │   └── websocket/
│   ├── src/main/resources/application.properties
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── Dockerfile
│   └── .dockerignore
├── frontend/
│   └── src/app/
│       ├── core/
│       ├── models/
│       └── features/
├── database/chat-iniziali.sql
├── compose.yaml
├── .env.example
├── .env
└── .gitignore
```

## Task 2 — Avviare backend e database nei container

Usa il `compose.yaml`, il `Dockerfile` e l'`application.properties` presenti nelle basi. Sostituisci il contenuto dell'`application.properties` generato dall'estensione di VSCode con quello allegato. In questo file ogni impostazione è scritta nella forma `chiave=valore`.

Il file Compose definisce:

- `db`: MySQL con database `multichat` e volume per conservare i dati.
- `backend`: Spring Boot, compilato nel container e raggiungibile dal PC sulla porta `8080`.

Il backend si collega a `db:3306`, usando il nome del servizio Docker. DBeaver si collega dal PC a `127.0.0.1:3307`: Compose associa questa porta alla `3306` del container MySQL. Angular chiama invece `http://localhost:8080/api` dal browser. La configurazione `restart: on-failure` fa ripartire Spring se MySQL è ancora in avvio.

Dalla root del progetto crea il file delle variabili d'ambiente e avvia i container:

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

### Come usare `.env`

Il file `.env` contiene i valori locali usati da Docker Compose, per esempio nome del database, utente e password. Il comando `Copy-Item` crea la tua copia a partire da `.env.example`, che è il modello da condividere con il progetto. Modifica i valori in `.env` secondo la tua configurazione; le credenziali già presenti sono destinate all'esercizio locale.

Quando avvii Compose dalla root, Compose legge `.env` e sostituisce i riferimenti come `${MYSQL_PASSWORD}` nel `compose.yaml`. La sezione `environment` passa poi i valori ai container. Spring li riceve attraverso proprietà come:

```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

Spring non legge direttamente il file `.env`: in questo progetto è Docker Compose a passargli le variabili. Il database conserva i dati nel volume anche quando arresti i container; modificare la password in `.env` non cambia automaticamente quella di un database già inizializzato.

### Come usare `.gitignore`

Il file `.gitignore` indica a Git quali file locali non includere nel repository. La base contiene:

```gitignore
.env
**/target/
**/node_modules/
**/dist/
**/.angular/
```

La prima riga esclude `.env`, che può contenere credenziali personali. Le altre escludono file compilati, dipendenze scaricate e cache, che gli strumenti possono ricreare. Il prefisso `**/` applica la regola anche nelle sottocartelle.

Condividi invece `.env.example`, `.gitignore`, `pom.xml`, `package.json` e `package-lock.json`. Il `.gitignore` non rimuove dal repository file già tracciati: serve a evitare l'aggiunta di nuovi file corrispondenti alle regole.

### Comandi durante lo sviluppo

Per applicare modifiche al codice Java:

```powershell
docker compose up -d --build backend
```

Per arrestare i container conservando il database:

```powershell
docker compose down
```

Per avviare Angular, apri un secondo terminale nella root:

```powershell
cd frontend
ng.cmd serve --host localhost --port 4200
```

L'applicazione sarà disponibile su `http://localhost:4200`. Angular rimane in esecuzione nel terminale locale.

## Task 3 — Modellare utenti, token, chat e messaggi

Crea le entità JPA e i relativi repository. L'`application.properties` allegato usa `spring.jpa.hibernate.ddl-auto=update`: per questo esercizio Hibernate crea e aggiorna le tabelle a partire dalle entità.

| Entità / tabella | Campi |
|---|---|
| `User` / `users` | `id`, `username`, `email`, `passwordHash` |
| `AuthToken` / `auth_tokens` | `token`, `user` |
| `Chat` / `chats` | `id`, `slug`, `title`, `description` |
| `Message` / `messages` | `id`, `chat`, `author`, `content`, `createdAt` |

1. Usa ID numerici auto-incrementali per utenti, chat e messaggi.
2. Usa il campo `token`, stringa di 36 caratteri, come chiave primaria di `auth_tokens`.
3. Collega ciascun token a un utente con una relazione `ManyToOne`. Un utente può avere più token, uno per ciascun login.
4. Collega ciascun messaggio alla chat e all'autore con relazioni `ManyToOne`.
5. Rendi univoci email, username e slug della chat. Salva email e username senza spazi esterni e in minuscolo.
6. Usa `Instant` per la data del messaggio, come spiegato di seguito.
7. Inserisci le tre chat direttamente nel database con query SQL eseguite da DBeaver, seguendo i passaggi sotto.

### Che cosa fa `Instant`

`Instant` è una classe di `java.time` che rappresenta un momento preciso sulla linea del tempo, indipendente dal fuso orario dell'utente. È adatta a registrare **quando è stato creato un messaggio**. Nel JSON, un valore come `2026-10-02T10:30:00Z` indica le 10:30 in UTC: la `Z` identifica questo riferimento temporale.

Dichiara nell'entità `Message` un campo:

```java
import java.time.Instant;

// All'interno dell'entità Message:
private Instant createdAt;
```

Nel service, quando costruisci un nuovo messaggio e prima di salvarlo:

```java
message.setCreatedAt(Instant.now());
```

`Instant.now()` legge l'istante corrente dall'orologio del server. Tutti i messaggi ricevono così la data dal backend; Angular invia soltanto il testo. Non usare la data fornita dal browser, che può avere un orologio diverso. In Angular il timestamp arriva come stringa e può essere mostrato nell'ora locale con `DatePipe`, per esempio `{{ message.createdAt | date:'dd/MM/yyyy HH:mm' }}`. Importa `DatePipe` da `@angular/common` negli `imports` del componente standalone.

Riferimento: [Instant in Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Instant.html).

### Inserire le chat con DBeaver

Mappa l'entità `Chat` sulla tabella `chats` con `@Table(name = "chats")`, usando le colonne `id`, `slug`, `title` e `description`. L'ID è auto-incrementale e lo slug è univoco. Lo **slug** è un identificatore testuale breve, per esempio `tempo-libero`.

Dopo aver creato l'entità, ricostruisci il backend con `docker compose up -d --build backend`: al suo avvio Hibernate crea la tabella. Poi apri DBeaver e crea una nuova connessione di tipo **MySQL** con questi valori:

| Campo | Valore |
|---|---|
| Host | `127.0.0.1` |
| Porta | `3307` |
| Database | `multichat` |
| Username | Valore di `MYSQL_USER` in `.env`, inizialmente `chat_app` |
| Password | Valore di `MYSQL_PASSWORD` in `.env` |

Salva e apri la connessione. Dalla connessione apri **SQL Editor → New SQL Script** e usa il file allegato [chat-iniziali.sql](basi-progetto/database/chat-iniziali.sql). Esegui lo script con l'azione **Execute SQL Script**; se hai disattivato l'auto-commit, esegui anche il commit.

Questa è la query per la prima chat; lo script contiene anche le altre due:

```sql
INSERT INTO chats (slug, title, description)
SELECT 'generale', 'Generale', 'Discussioni e domande generali'
WHERE NOT EXISTS (SELECT 1 FROM chats WHERE slug = 'generale');
```

La query inserisce la riga solo quando lo slug non è già presente. L'ID viene assegnato da MySQL. L'inserimento iniziale avviene manualmente da DBeaver, senza componenti Java che creano le chat all'avvio. Riferimento: [editor ed esecuzione SQL in DBeaver](https://dbeaver.com/docs/dbeaver/SQL-Execution/).

| Slug | Titolo | Descrizione |
|---|---|---|
| `generale` | Generale | Discussioni e domande generali |
| `tecnologia` | Tecnologia | Informatica, strumenti e novità tecnologiche |
| `tempo-libero` | Tempo libero | Hobby, sport e interessi personali |

Le chat vengono lette dal database: non scrivere il loro elenco direttamente nel codice Angular. In questa versione tutti gli utenti autenticati possono accedere a tutte le chat.

## Task 4 — Realizzare registrazione e login con token nel database

Crea controller, service e DTO per queste API:

| Metodo e URL | Body | Risposta |
|---|---|---|
| `POST /api/auth/register` | `username`, `email`, `password` | `201`, dati dell'utente senza password |
| `POST /api/auth/login` | `email`, `password` | `200`, `{ "token": "stringa-generata" }` |
| `GET /api/auth/me` | Nessuno | `200`, `id`, `username`, `email` dell'utente autenticato |
| `POST /api/auth/logout` | Nessuno | `204`, senza body |

Per la registrazione:

1. Richiedi username, email e password. Lo username deve avere da 3 a 30 caratteri, l'email deve avere un formato valido e la password almeno 8 caratteri, con massimo 72 byte UTF-8 per BCrypt.
2. Usa il `PasswordEncoder` già configurato nelle basi per salvare l'hash della password.
3. Restituisci `400` per dati non validi e `409` se email o username sono già presenti.

Per il login:

1. Cerca l'utente tramite email.
2. Confronta la password ricevuta con l'hash usando `passwordEncoder.matches(...)`.
3. Se le credenziali sono errate, restituisci `401`.
4. Se sono corrette, chiama `TokenService.createToken(userId)`.
5. Genera una stringa con `UUID.randomUUID().toString()`, salvala in `auth_tokens` insieme all'utente e restituiscila al frontend.

Completa i `TODO AUTH-01`–`AUTH-04` di [TokenService.java](basi-progetto/backend/src/main/java/com/progettiportfoglio/multichat/auth/TokenService.java): aggiungi il repository e implementa salvataggio, ricerca per token ed eliminazione. Il token è una semplice stringa: non contiene informazioni da decodificare e rimane valido finché esiste nel database.

Per il logout, leggi il bearer dall'header `Authorization` ed elimina soltanto la riga corrispondente. Puoi usare il metodo `TokenAuthenticationFilter.extractBearerToken(...)` già fornito. Il frontend chiamerà questa API prima di cancellare il token dalla propria memoria.

## Task 5 — Completare il filter e l'interceptor

### Backend

Parti da [TokenAuthenticationFilter.java](basi-progetto/backend/src/main/java/com/progettiportfoglio/multichat/security/TokenAuthenticationFilter.java). Il filter è già collegato a Spring Security e legge l'header:

```http
Authorization: Bearer stringa-generata-al-login
```

Il flusso da completare attraverso `TokenService` è:

1. Cercare nel database il token ricevuto, confrontandolo con il valore salvato.
2. Recuperare l'utente associato.
3. Restituire un `AuthenticatedUser` al filter, che imposta l'autenticazione di Spring.
4. Se il token non esiste, lasciare la richiesta non autenticata: le API protette rispondono `401`.

La `SecurityConfig` allegata lascia pubblici login, registrazione e apertura del WebSocket. Il WebSocket autentica poi il token nel proprio messaggio di connessione. Il filter non deve avere `@Component`: viene già creato nella configurazione.

Nei controller puoi ottenere l'utente corrente con:

```java
@AuthenticationPrincipal AuthenticatedUser currentUser
```

Usa `currentUser.id()` per conoscere l'ID dell'utente, senza chiederlo al frontend.

### Frontend

Parti da [auth.interceptor.ts](basi-progetto/frontend/src/app/core/auth.interceptor.ts) e completa `TODO HTTP-01` aggiungendo il token alla copia della richiesta HTTP. L'interceptor è già predisposto per intervenire soltanto sulle API del progetto, escludendo login e registrazione.

In `frontend/src/app/app.config.ts`, aggiungi gli import:

```typescript
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth.interceptor';
```

Aggiungi poi questo provider all'array `providers` esistente, mantenendo quelli già generati:

```typescript
provideHttpClient(withInterceptors([authInterceptor]))
```

Usa `TokenStorageService.saveToken(...)` dopo il login. La base salva il valore in `sessionStorage` e lo mantiene durante il refresh della scheda. Completa anche `TODO HTTP-02` per mostrare un messaggio quando una richiesta protetta riceve `401`.

## Task 6 — Creare le API delle chat e dei messaggi

Implementa queste API protette:

| Metodo e URL | Operazione |
|---|---|
| `GET /api/chats` | Restituire tutte le chat |
| `GET /api/chats/{chatId}` | Restituire i dettagli della chat |
| `GET /api/chats/{chatId}/messages` | Restituire tutta la cronologia della chat |
| `POST /api/chats/{chatId}/messages` | Salvare un messaggio con body `{ "content": "Ciao!" }` |

Per ogni chat restituisci `id`, `slug`, `title` e `description`.

Per i messaggi usa il `MessageDto` allegato, con questo formato JSON:

```json
{
  "id": 15,
  "chatId": 1,
  "authorId": 3,
  "username": "mario",
  "content": "Ciao a tutti!",
  "createdAt": "2026-10-02T10:30:00Z"
}
```

1. Recupera i messaggi ordinandoli dal più vecchio al più recente, per data e poi ID.
2. Restituisci l'intera cronologia, compresi i messaggi scritti prima della registrazione dell'utente. Non introdurre paginazione in questa versione.
3. Per una chat vuota restituisci `[]`; per una chat inesistente restituisci `404`.
4. In fase di invio accetta testo da 1 a 2000 caratteri dopo la rimozione degli spazi esterni; restituisci `400` per contenuti non validi.
5. Assegna l'autore usando l'utente autenticato e la data usando l'orario del backend.
6. Salva tramite un metodo del service annotato `@Transactional`, che restituisce il DTO del messaggio.
7. Nel controller, dopo il ritorno del service e quindi dopo il commit, pubblica il DTO con `ChatWebSocketService.publishMessage(...)` e restituisci lo stesso DTO con stato `201`.

## Task 7 — Completare i servizi WebSocket

Il WebSocket serve a ricevere i nuovi messaggi senza ricaricare la pagina. L'invio di un messaggio da parte dell'utente usa la POST REST della task precedente.

### Backend

La configurazione allegata espone `/ws` e usa STOMP. Ogni chat ha un topic dedicato:

```text
/topic/chats/{chatId}/messages
```

Completa:

1. `TODO WS-01` in [WebSocketConfig.java](basi-progetto/backend/src/main/java/com/progettiportfoglio/multichat/config/WebSocketConfig.java): estrai l'ID dal topic e rifiuta la sottoscrizione se la chat non esiste.
2. `TODO WS-02` in [ChatWebSocketService.java](basi-progetto/backend/src/main/java/com/progettiportfoglio/multichat/websocket/ChatWebSocketService.java): usa `SimpMessagingTemplate.convertAndSend(...)` per inviare il DTO al topic della chat.

La base legge il token dal frame STOMP `CONNECT` e lo cerca nel database tramite `TokenService`. Consente di sottoscrivere i topic delle chat e impedisce al client di pubblicare direttamente sul broker.

### Frontend

Completa [chat-websocket.service.ts](basi-progetto/frontend/src/app/core/chat-websocket.service.ts):

1. `TODO WS-03`: aggiungi il token a `connectHeaders.Authorization` nel formato `Bearer ...`.
2. `TODO WS-04`: converti `frame.body` in un oggetto `ChatMessage` e inoltralo con `messageSubject.next(...)`; gestisci gli errori di parsing.

L'interceptor HTTP non aggiunge header ai messaggi WebSocket: il token va passato anche in `connectHeaders`. La base contiene già connessione, cambio chat, riconnessione e disconnessione.

## Task 8 — Creare registrazione, login e navigazione

Crea queste pagine e route Angular:

| Route | Contenuto |
|---|---|
| `/register` | Form con username, email e password |
| `/login` | Form con email e password |
| `/chats` | Elenco delle tre chat |
| `/chats/:chatId` | Conversazione della chat selezionata |

1. Usa Reactive Forms per i form e mostra gli errori vicino ai campi.
2. Crea un `AuthService` per chiamare registrazione, login, utente corrente e logout usando `HttpClient`.
3. Dopo la registrazione porta l'utente al login; dopo il login salva il token e apri `/chats`.
4. Aggiungi un guard per le pagine delle chat. Se manca il token, rimanda a `/login`.
5. Se esiste un token dopo un refresh, recupera l'utente con `/api/auth/me`.
6. Aggiungi un pulsante logout: chiama la relativa API, cancella il token locale, chiudi il WebSocket, svuota i dati dell'utente e torna al login.

Il backend controlla l'accesso ai dati anche quando il frontend ha già applicato il guard.

### Come funzionano i Reactive Forms

Con i Reactive Forms descrivi i campi e le loro regole in TypeScript e colleghi gli input HTML a quel modello. Angular mantiene sincronizzati il testo digitato, il valore del campo e il suo stato di validità.

- **`FormControl`** rappresenta un campo, per esempio l'email.
- **`FormGroup`** raccoglie più controlli e rappresenta il form completo.
- **`ReactiveFormsModule`** rende disponibili i collegamenti da usare nel template; aggiungilo agli `imports` del componente standalone.

Le basi includono [login.ts](basi-progetto/frontend/src/app/features/auth/login/login.ts) e [login.html](basi-progetto/frontend/src/app/features/auth/login/login.html). Il modello del form è già impostato: completa `TODO FORM-01` per collegarlo al tuo `AuthService`. Per aprirlo nella route `/login`, usa la classe `Login`, ad esempio:

```typescript
{
  path: 'login',
  loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
}
```

Nel codice del componente, il controllo email è costruito così:

```typescript
email: new FormControl('', {
  nonNullable: true,
  validators: [Validators.required, Validators.email],
})
```

La stringa vuota è il valore iniziale. `nonNullable: true` mantiene il valore di tipo `string`, anche dopo un reset, senza aggiungere il caso `null`. `form.getRawValue()` restituisce un oggetto tipizzato con i valori dei campi, in questo caso `{ email, password }`, da passare al servizio di login. Riferimento: [Reactive Forms tipizzati](https://angular.dev/guide/forms/typed-forms).

Nel template, `[formGroup]="form"` collega l'elemento `<form>` al modello, mentre `formControlName="email"` collega l'input al controllo con quel nome. `(ngSubmit)="submit()"` chiama il metodo del componente quando l'utente invia il form. Non aggiungere `ngModel` sugli stessi input. Riferimento: [Reactive Forms](https://angular.dev/guide/forms/reactive-forms).

### Validatori ed errori vicino ai campi

`Validators.required` richiede un valore; `Validators.email` controlla il formato; `Validators.minLength(8)` richiede almeno otto caratteri e va usato per la password di registrazione. Queste regole validano i dati nel browser; il backend applica comunque le proprie regole.

Ogni controllo espone informazioni utili:

| Proprietà o metodo | Significato |
|---|---|
| `valid` / `invalid` | Il valore rispetta o non rispetta i validatori |
| `touched` | L'utente è entrato nel campo e poi ne è uscito |
| `dirty` | L'utente ha modificato il valore tramite l'interfaccia |
| `hasError('required')` | È presente l'errore di campo obbligatorio |

Mostra un errore dopo che l'utente ha interagito con il campo, per esempio:

```html
@if (form.controls.email.touched && form.controls.email.hasError('required')) {
  <p>Inserisci l'email.</p>
}
```

All'inizio di `submit()`, chiama `form.markAllAsTouched()` e interrompi l'invio se `form.invalid`. In questo modo anche un invio del form vuoto mostra gli errori. Una risposta `401` del server, invece, va mostrata come errore generale di credenziali. Riferimento: [validazione dei form Angular](https://angular.dev/guide/forms/form-validation).

### Signal e Angular 22

Usa componenti standalone, `inject()` per ottenere i servizi e la sintassi `@if` / `@for` nei template. Per lo stato visualizzato usa **signal**, come negli allegati: `submitting` ed `error` nel login, `token` nel servizio di sessione, `connected` ed `error` nel servizio WebSocket.

Un signal contiene un valore: `submitting()` lo legge, `submitting.set(true)` lo cambia. Angular aggiorna le parti del template che lo leggono. `computed(...)` serve per valori derivati: nelle basi `hasToken` dipende da `token`. Riferimento: [signal Angular](https://angular.dev/guide/signals).

I Reactive Forms gestiscono i campi; i signal gestiscono lo stato circostante. Nel login, imposta `submitting` durante la chiamata HTTP e ripristinalo con `finalize`, così il pulsante torna disponibile sia dopo un successo sia dopo un errore. Non creare `computed(() => form.valid)`: `valid` è una proprietà del form, non un signal. Per reagire ai cambiamenti del form esistono gli Observable `valueChanges` e `statusChanges`.

Mantieni la configurazione zoneless generata da Angular 22. Aggiornare un signal letto dal template rende visibili anche i cambiamenti provenienti dalle callback HTTP e WebSocket. Riferimento: [Angular zoneless](https://angular.dev/guide/zoneless).

## Task 9 — Creare la pagina della conversazione

Nell'elenco mostra titolo e descrizione delle chat. Nella conversazione mostra titolo, cronologia, campo di testo e pulsante di invio.

1. Leggi `chatId` dalla route e recupera i dati della chat.
2. Sottoscrivi gli eventi `messages$` e `chatReady$` del servizio WebSocket **prima** di chiamare `connectToChat(chatId)`. Leggi invece lo stato dai signal `connected()` ed `error()` nel template.
3. Quando `chatReady$` emette l'ID della chat aperta, carica la cronologia REST. Lo stesso evento consente di ricaricarla dopo una riconnessione.
4. Conserva la lista visualizzata in un `signal<ChatMessage[]>([])`. Unisci cronologia, risposta della POST ed eventi WebSocket usando l'ID del messaggio per eliminare i duplicati. Aggiorna il signal con `.set(...)` o `.update(...)`, creando un nuovo array; non usare soltanto `.push(...)` sull'array esistente. Non sovrascrivere i messaggi arrivati mentre lo storico è in caricamento.
5. Mostra username, testo e data/ora per ogni messaggio. Mantieni tutta la cronologia raggiungibile scorrendo la pagina.
6. Visualizza i messaggi come testo tramite interpolazione Angular, senza interpretarli come HTML.
7. Invia il testo tramite POST; svuota il campo soltanto dopo una risposta positiva e conserva il testo in caso di errore.
8. Mostra uno stato di caricamento, il messaggio di chat vuota e un avviso quando la connessione viene interrotta.
9. Aggiungi un pulsante per ricaricare la cronologia dal database.
10. Quando lasci la conversazione, chiama `leaveChat()` e annulla le sottoscrizioni del componente, per esempio con `takeUntilDestroyed`. Se la route cambia solo parametro, gestisci esplicitamente il cambio di chat e ignora eventuali risposte HTTP relative alla chat precedente.

Nel template usa `@for (message of messages(); track message.id)` per mostrare l'elenco. Gli Observable del servizio rappresentano singoli eventi WebSocket; il signal del componente conserva lo stato completo della conversazione. Mantieni gli eventi come flusso, così ogni messaggio ricevuto viene elaborato.

Le basi forniscono i collegamenti tecnici; entità, repository, controller, logica applicativa e pagine restano da realizzare completando le task e i `TODO` nei file allegati.
