# JARVIS Mobile Controller - Contributing Guidelines

## Welcome to JARVIS Development!

Thank you for your interest in contributing to JARVIS Mobile Controller. This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Report security issues privately
- Focus on code quality and user experience

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Jarvis-Gujjar-`
3. Create a feature branch: `git checkout -b feature/your-feature`
4. Make your changes
5. Commit: `git commit -am 'Add feature'`
6. Push: `git push origin feature/your-feature`
7. Submit a Pull Request

## Development Standards

### Code Style
- Follow Kotlin style guide
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use proper error handling

### Architecture
- Maintain clean architecture layers
- Use dependency injection via Hilt
- Follow MVVM pattern
- Keep business logic in use cases
- Use Flow for reactive data

### Testing
- Write unit tests for use cases
- Write UI tests for screens
- Aim for >80% code coverage
- Test edge cases and error scenarios
- Use meaningful test names

### Git Practices
- Keep commits atomic and focused
- Write descriptive commit messages
- Rebase before submitting PR
- Resolve conflicts locally
- Keep branch up to date with main

## Pull Request Process

### Before Submitting
1. Run all tests locally: `./gradlew test`
2. Run lint checks: `./gradlew lint`
3. Build release APK: `./gradlew assembleRelease`
4. Verify no compile errors
5. Update documentation if needed

### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Code refactoring

## Testing
Describe test coverage for changes

## Screenshots (if UI changes)
Add relevant screenshots

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes
- [ ] Code follows style guide
- [ ] All tests pass
```

## Feature Development

### Creating a New Feature

1. **Domain Layer**
   - Add model in `domain/model/`
   - Add repository interface
   - Add use cases

2. **Data Layer**
   - Implement repository
   - Add database entities if needed
   - Add API services if needed

3. **Presentation Layer**
   - Create ViewModel
   - Create Compose UI
   - Add navigation
   - Add tests

4. **Documentation**
   - Update README
   - Update API_REFERENCE
   - Add code comments

### Example: Adding a "Reminders" Feature

**Domain Layer** (`domain/src/main/java/...`)
```kotlin
// domain/model/ReminderModels.kt
data class Reminder(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val isCompleted: Boolean
)

// domain/repository/ReminderRepository.kt
interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Unit>
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun updateReminder(reminder: Reminder): Result<Unit>
    suspend fun deleteReminder(id: String): Result<Unit>
}

// domain/usecase/ReminderUseCases.kt
class CreateReminderUseCase(repository: ReminderRepository) { ... }
class GetRemindersUseCase(repository: ReminderRepository) { ... }
```

**Data Layer** (`data/src/main/java/...`)
```kotlin
// data/db/entity/ReminderEntity.kt
@Entity(tableName = "reminders")
data class ReminderEntity(...)

// data/db/dao/ReminderDao.kt
@Dao
interface ReminderDao { ... }

// data/repository/ReminderRepositoryImpl.kt
class ReminderRepositoryImpl(...) : ReminderRepository { ... }
```

**Presentation Layer** (`app/src/main/java/...`)
```kotlin
// presentation/viewmodel/ReminderViewModel.kt
@HiltViewModel
class ReminderViewModel @Inject constructor(...) : ViewModel() { ... }

// presentation/screens/ReminderScreen.kt
@Composable
fun ReminderScreen(navController: NavHostController) { ... }
```

## Bug Reports

### Reporting a Bug

1. Check existing issues first
2. Provide clear description
3. Include steps to reproduce
4. Attach screenshots/logs
5. Specify device and Android version
6. Include any relevant code

### Bug Report Template
```markdown
## Description
Brief description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happened

## Device Information
- Device: [e.g., Pixel 6]
- Android Version: [e.g., 13]
- App Version: [e.g., 1.0.0]

## Screenshots/Logs
Attach relevant files
```

## Feature Requests

### Requesting a Feature

1. Check if feature exists
2. Describe use case
3. Explain benefits
4. Provide examples
5. Consider implementation

### Feature Request Template
```markdown
## Description
What feature would you like?

## Use Case
Why do you need this feature?

## Proposed Solution
How should it work?

## Alternatives Considered
Any alternative approaches?

## Additional Context
Any other information?
```

## Documentation Contributions

Good documentation is crucial!

- Update README for user-facing changes
- Update API_REFERENCE for API changes
- Update DEVELOPMENT.md for dev changes
- Add code comments for complex logic
- Keep documentation current

## Performance Guidelines

- Profile before optimizing
- Use Android Profiler
- Minimize database queries
- Cache when appropriate
- Use efficient data structures
- Monitor memory usage
- Keep frame rate smooth

## Security Guidelines

- Never commit secrets
- Use encryption for sensitive data
- Validate all user input
- Use HTTPS for APIs
- Follow principle of least privilege
- Report security issues privately
- Review SECURITY.md

## Review Process

1. Code review by maintainers
2. Feedback and discussions
3. Make requested changes
4. Approval and merge
5. Feature released in next version

## Getting Help

- Check documentation first
- Search existing issues
- Ask in discussions
- Tag maintainers if needed
- Be patient and respectful

## Recognition

Contributors will be recognized in:
- CHANGELOG.md
- Release notes
- Contributors section

## Questions?

Feel free to:
- Open an issue
- Start a discussion
- Email: fizannawazgujjar@gmail.com

Thank you for contributing to JARVIS! 🚀
