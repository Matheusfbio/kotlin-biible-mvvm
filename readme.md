Perfeito! Bora criar um README completo que resuma todo o fluxo do seu app Android MVVM com Jetpack Compose, Retrofit e Room, já incluindo a busca de versículos pela Bíblia. Vou deixar bem detalhado, passo a passo, pronto pra você colocar no GitHub ou enviar pra qualquer dev.

---

# Bíblia API MVVM - Android Compose

Aplicativo Android em **Kotlin** que consome a **Bible API**, armazena localmente usando **Room Database**, e exibe dados com **Jetpack Compose**. Arquitetura usada: **MVVM**.

## 🔹 Funcionalidades

* Buscar versículos específicos da Bíblia.
* Pesquisar qualquer passagem (ex: `john 3:16` ou `psalm 23:1`).
* Armazenar localmente no **Room** para leitura offline.
* Exibir lista de versículos com UI responsiva via **Jetpack Compose**.
* Indicador de carregamento e tratamento de erros.

---

## 📂 Estrutura de Pastas

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── seu_pacote/
        │           ├── data/
        │           │   ├── model/          <-- Classes de dados (API + Room)
        │           │   │   ├── BibleVerse.kt
        │           │   │   └── Verse.kt
        │           │   ├── local/          <-- Room
        │           │   │   ├── AppDatabase.kt
        │           │   │   ├── VerseDao.kt
        │           │   │   └── VerseEntity.kt
        │           │   └── remote/         <-- Retrofit
        │           │       ├── BibleApiService.kt
        │           │       └── RetrofitInstance.kt
        │           ├── repository/          <-- Repository (API + Room)
        │           │   └── BibleRepository.kt
        │           ├── ui/                  <-- Compose UI
        │           │   └── screen/
        │           │       └── BibleVerseScreen.kt
        │           ├── viewmodel/           <-- ViewModel
        │           │   └── BibleViewModel.kt
        │           └── MainActivity.kt      <-- Entry point do app
        └── res/
            ├── layout/                     <-- Para XMLs (opcional)
            ├── values/
            └── drawable/
```

---

## ⚡ Dependências

Adicione no `build.gradle (Module: app)`:

```gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Kotlin Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'

    // Jetpack Compose
    implementation 'androidx.compose.ui:ui:1.5.0'
    implementation 'androidx.compose.material:material:1.5.0'
    implementation 'androidx.compose.ui:ui-tooling-preview:1.5.0'
    implementation 'androidx.activity:activity-compose:1.9.0'

    // ViewModel + Lifecycle
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'

    // Room
    implementation "androidx.room:room-runtime:2.6.0"
    kapt "androidx.room:room-compiler:2.6.0"
    implementation "androidx.room:room-ktx:2.6.0"
}
```

---

## 🏗 Arquitetura MVVM

**Fluxo de dados:**

```
View (Compose UI)
   ↑
ViewModel (estado e lógica)
   ↑
Repository (decide se pega do Room ou API)
   ↑
Retrofit / Room Database
```

---

## 🔹 Modelo de Dados

```kotlin
data class BibleVerse(
    val reference: String,
    val verses: List<Verse>,
    val text: String
)

data class Verse(
    val book_id: String,
    val book_name: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)
```

**Room Entity:**

```kotlin
@Entity(tableName = "verses")
data class VerseEntity(
    @PrimaryKey val id: String,
    val reference: String,
    val text: String
)
```

**DAO:**

```kotlin
@Dao
interface VerseDao {
    @Query("SELECT * FROM verses")
    fun getAll(): Flow<List<VerseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<VerseEntity>)
}
```

**Database:**

```kotlin
@Database(entities = [VerseEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun verseDao(): VerseDao
}
```

---

## 🔹 Retrofit API

```kotlin
interface BibleApiService {
    @GET("{passage}")
    suspend fun getVerse(@Path("passage") passage: String): BibleVerse
}

object RetrofitInstance {
    val api: BibleApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://bible-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BibleApiService::class.java)
    }
}
```

---

## 🔹 Repository

```kotlin
class BibleRepository(private val api: BibleApiService) {
    suspend fun getVerse(passage: String): BibleVerse {
        return api.getVerse(passage)
    }
}
```

---

## 🔹 ViewModel

```kotlin
class BibleViewModel(private val repository: BibleRepository) : ViewModel() {

    private var _verse = mutableStateOf<BibleVerse?>(null)
    val verse: State<BibleVerse?> get() = _verse

    private var _loading = mutableStateOf(false)
    val loading: State<Boolean> get() = _loading

    private var _error = mutableStateOf<String?>(null)
    val error: State<String?> get() = _error

    fun fetchVerse(passage: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getVerse(passage)
                _verse.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _verse.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
```

---

## 🔹 Compose UI com Busca

```kotlin
@Composable
fun BibleVerseScreen(viewModel: BibleViewModel) {

    val verse by viewModel.verse
    val loading by viewModel.loading
    val error by viewModel.error

    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bíblia API MVVM", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Digite a passagem (ex: john 3:16)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { if (inputText.isNotBlank()) viewModel.fetchVerse(inputText) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Versículo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> CircularProgressIndicator()
            error != null -> Text("Erro: $error", color = Color.Red)
            verse != null -> verse!!.verses.forEach { v ->
                Text("${v.book_name} ${v.chapter}:${v.verse}")
                Text(v.text)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
```

---

## 🔹 MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BibleRepository(RetrofitInstance.api)
        val viewModel = BibleViewModel(repository)

        setContent {
            BibleVerseScreen(viewModel)
        }
    }
}
```

---

## ✅ Próximos Passos

* Integrar **Room Database** no Repository para armazenamento offline.
* Melhorar UI usando **LazyColumn** para múltiplos versículos.
* Tratar erros mais detalhadamente e mostrar mensagens amigáveis.
* Adicionar **Pull-to-Refresh** ou cache com expiração.
