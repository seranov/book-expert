# VersionCascader - Maven Version Update Utility

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
