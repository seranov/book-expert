# VersionCascader - Maven Version Update Utility

**English** | [Русский](#versioncascader---maven-version-update-utility-ru)

Automatic cascading version updates for multi-module Maven projects.

## Description

VersionCascader is a JBang utility for automatically detecting and updating dependency versions in multi-module Maven projects. The tool:

1. Scans all `pom.xml` files in the project
2. Builds a dependency graph between modules
3. Finds version mismatches (when a module version differs from dependency references)
4. Updates dependency versions to current versions
5. Automatically increments patch versions for modules with changed dependencies
6. Cascades changes through all dependent modules

## Installing JBang

### Linux / macOS

```bash
# Using curl
curl -Ls https://sh.jbang.dev | bash -s - app setup

# Or using SDK Man
sdk install jbang
```

### Windows

```powershell
# Using PowerShell
iex "& { $(iwr https://ps.jbang.dev) } app setup"

# Or using Chocolatey
choco install jbang
```

### Verify Installation

```bash
jbang version
```

## Usage

### Basic Usage

Run the utility from the root of your multi-module Maven project:

```bash
jbang tools/VersionCascader.java
```

### Command Line Options

```bash
# Show what would change without applying changes
jbang tools/VersionCascader.java --dry-run

# Verbose output for debugging
jbang tools/VersionCascader.java --verbose

# Specify path to project
jbang tools/VersionCascader.java --path /path/to/your/maven/project

# Combine options
jbang tools/VersionCascader.java --dry-run --verbose --path /path/to/project

# Show help
jbang tools/VersionCascader.java --help
```

## Algorithm

```
1. Scan all pom.xml files in project directory (excluding target/)
2. Extract for each module:
   - groupId, artifactId, version
   - List of dependencies with their versions
3. Build dependency graph (who depends on whom)
4. Detect version mismatches:
   - Module A has version 1.2.3
   - Module B depends on A version 1.2.2
5. Update dependency A version in module B to 1.2.3
6. Increment patch version of module B (X.Y.Z → X.Y.(Z+1))
7. Recursively repeat for all modules depending on B
8. Save modified pom.xml files
```

## Example Output

```
[INFO] Scanning project at: /home/user/my-project
[INFO] Found 15 modules
[INFO] Building dependency graph...
[INFO] 
[INFO] Detected version mismatches:
[INFO]   com.example:lib-core 1.2.3 (was 1.2.2 in: service-api, service-web)
[INFO]
[INFO] Updating dependencies:
[INFO]   service-api/pom.xml: lib-core 1.2.2 → 1.2.3
[INFO]   service-api/pom.xml: version 2.0.5 → 2.0.6 (patch increment)
[INFO]   service-web/pom.xml: lib-core 1.2.2 → 1.2.3
[INFO]   service-web/pom.xml: version 1.1.0 → 1.1.1 (patch increment)
[INFO]
[INFO] Cascade updates:
[INFO]   service-gateway/pom.xml: service-api 2.0.5 → 2.0.6
[INFO]   service-gateway/pom.xml: version 3.0.0 → 3.0.1 (patch increment)
[INFO]
[INFO] Summary: Updated 3 modules, 4 dependency references
```

## Technical Details

### Dependencies

The utility uses the following libraries (automatically downloaded by JBang):

- `org.dom4j:dom4j:2.1.4` — XML parsing
- `info.picocli:picocli:4.7.5` — CLI interface
- `jaxen:jaxen:2.0.0` — XPath support for dom4j

### Requirements

- **Java 17+**
- **JBang** (installation described above)
- Multi-module Maven project with `pom.xml` files

### Caching

Dependencies are cached in:
- **Linux/macOS**: `~/.jbang/cache`
- **Windows**: `%USERPROFILE%\.jbang\cache`

Maven dependencies use the standard repository `~/.m2`

## Limitations

1. The utility only works with explicitly specified dependency versions
2. Does not process:
   - Versions from properties (`${project.version}`)
   - Dependency management without explicit versions
   - External dependencies (not from current project)
3. Only increments patch version (third component: X.Y.Z → X.Y.Z+1)

## CI/CD Usage

### GitLab CI

```yaml
version-update:
  stage: build
  image: eclipse-temurin:17-jdk
  before_script:
    - curl -Ls https://sh.jbang.dev | bash -s - app setup
    - export PATH="$HOME/.jbang/bin:$PATH"
  script:
    - jbang tools/VersionCascader.java --verbose
    - git diff  # Show changes
  only:
    - merge_requests
```

### GitHub Actions

```yaml
name: Update Versions
on: [pull_request]

jobs:
  update-versions:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Install JBang
        run: |
          curl -Ls https://sh.jbang.dev | bash -s - app setup
          echo "$HOME/.jbang/bin" >> $GITHUB_PATH
      - name: Run VersionCascader
        run: jbang tools/VersionCascader.java --verbose
```

## Development and Debugging

### Dry-run Mode

To check changes without actually writing:

```bash
jbang tools/VersionCascader.java --dry-run --verbose
```

### Verbose Mode

For detailed logging:

```bash
jbang tools/VersionCascader.java --verbose
```

## License

This tool is part of the book-expert project.

## Support

If you find issues, please create an issue in the project repository.

---

<a name="versioncascader---maven-version-update-utility-ru"></a>
# VersionCascader - Maven Version Update Utility (RU)

Автоматическое каскадное обновление версий в многомодульных Maven проектах.

## Описание

VersionCascader — это JBang утилита для автоматического обнаружения и обновления версий зависимостей в многомодульных Maven проектах. Утилита:

1. Сканирует все `pom.xml` файлы в проекте
2. Строит граф зависимостей между модулями
3. Находит несоответствия версий (когда версия модуля отличается от версии в зависимостях)
4. Обновляет версии зависимостей до актуальных
5. Автоматически инкрементирует patch-версию для модулей с изменёнными зависимостями
6. Каскадно распространяет изменения по всем зависимым модулям

## Установка JBang

### Linux / macOS

```bash
# Используя curl
curl -Ls https://sh.jbang.dev | bash -s - app setup

# Или используя SDK Man
sdk install jbang
```

### Windows

```powershell
# Используя PowerShell
iex "& { $(iwr https://ps.jbang.dev) } app setup"

# Или используя Chocolatey
choco install jbang
```

### Проверка установки

```bash
jbang version
```

## Использование

### Базовое использование

Запустите утилиту из корня вашего многомодульного Maven проекта:

```bash
jbang tools/VersionCascader.java
```

### Опции командной строки

```bash
# Показать что изменится без применения изменений
jbang tools/VersionCascader.java --dry-run

# Подробный вывод для отладки
jbang tools/VersionCascader.java --verbose

# Указать путь к проекту
jbang tools/VersionCascader.java --path /path/to/your/maven/project

# Комбинация опций
jbang tools/VersionCascader.java --dry-run --verbose --path /path/to/project

# Показать справку
jbang tools/VersionCascader.java --help
```

## Алгоритм работы

```
1. Сканирование всех pom.xml файлов в директории проекта (исключая target/)
2. Извлечение для каждого модуля:
   - groupId, artifactId, version
   - Списка dependencies с их версиями
3. Построение графа зависимостей (кто от кого зависит)
4. Обнаружение несоответствий версий:
   - Модуль A имеет версию 1.2.3
   - Модуль B зависит от A версии 1.2.2
5. Обновление версии зависимости A в модуле B до 1.2.3
6. Инкрементирование patch-версии модуля B (X.Y.Z → X.Y.(Z+1))
7. Рекурсивное повторение для всех модулей, зависящих от B
8. Сохранение изменённых pom.xml файлов
```

## Пример вывода

```
[INFO] Scanning project at: /home/user/my-project
[INFO] Found 15 modules
[INFO] Building dependency graph...
[INFO] 
[INFO] Detected version mismatches:
[INFO]   com.example:lib-core 1.2.3 (was 1.2.2 in: service-api, service-web)
[INFO]
[INFO] Updating dependencies:
[INFO]   service-api/pom.xml: lib-core 1.2.2 → 1.2.3
[INFO]   service-api/pom.xml: version 2.0.5 → 2.0.6 (patch increment)
[INFO]   service-web/pom.xml: lib-core 1.2.2 → 1.2.3
[INFO]   service-web/pom.xml: version 1.1.0 → 1.1.1 (patch increment)
[INFO]
[INFO] Cascade updates:
[INFO]   service-gateway/pom.xml: service-api 2.0.5 → 2.0.6
[INFO]   service-gateway/pom.xml: version 3.0.0 → 3.0.1 (patch increment)
[INFO]
[INFO] Summary: Updated 3 modules, 4 dependency references
```

## Технические детали

### Зависимости

Утилита использует следующие библиотеки (автоматически загружаются JBang):

- `org.dom4j:dom4j:2.1.4` — парсинг XML
- `info.picocli:picocli:4.7.5` — CLI интерфейс
- `jaxen:jaxen:2.0.0` — XPath поддержка для dom4j

### Требования

- **Java 17+**
- **JBang** (установка описана выше)
- Многомодульный Maven проект с `pom.xml` файлами

### Кэширование

Зависимости кэшируются в:
- **Linux/macOS**: `~/.jbang/cache`
- **Windows**: `%USERPROFILE%\.jbang\cache`

Maven зависимости используют стандартный репозиторий `~/.m2`

## Ограничения

1. Утилита работает только с явно указанными версиями зависимостей
2. Не обрабатывает:
   - Версии из properties (`${project.version}`)
   - Dependency management без explicit версий
   - External dependencies (не из текущего проекта)
3. Инкрементирует только patch-версию (third component: X.Y.Z → X.Y.Z+1)

## Использование в CI/CD

### GitLab CI

```yaml
version-update:
  stage: build
  image: eclipse-temurin:17-jdk
  before_script:
    - curl -Ls https://sh.jbang.dev | bash -s - app setup
    - export PATH="$HOME/.jbang/bin:$PATH"
  script:
    - jbang tools/VersionCascader.java --verbose
    - git diff  # Показать изменения
  only:
    - merge_requests
```

### GitHub Actions

```yaml
name: Update Versions
on: [pull_request]

jobs:
  update-versions:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Install JBang
        run: |
          curl -Ls https://sh.jbang.dev | bash -s - app setup
          echo "$HOME/.jbang/bin" >> $GITHUB_PATH
      - name: Run VersionCascader
        run: jbang tools/VersionCascader.java --verbose
```

## Разработка и отладка

### Режим dry-run

Для проверки изменений без фактической записи:

```bash
jbang tools/VersionCascader.java --dry-run --verbose
```

### Verbose режим

Для детального логирования:

```bash
jbang tools/VersionCascader.java --verbose
```

## Лицензия

Этот инструмент является частью проекта book-expert.

## Поддержка

При обнаружении проблем создайте issue в репозитории проекта.
