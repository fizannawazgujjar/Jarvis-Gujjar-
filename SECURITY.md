# Security Policy

## Reporting Security Vulnerabilities

If you discover a security vulnerability in JARVIS Mobile Controller, please email security@jarvis-controller.dev instead of using the public issue tracker.

## Security Measures

### Data Protection
- All API keys are stored in encrypted SharedPreferences
- No sensitive data is logged
- Cleartext traffic is disabled by default
- HTTPS is enforced for all API communications

### Code Security
- ProGuard obfuscation in release builds
- Runtime permissions enforced
- Input validation on all user data
- No SQL injection vulnerabilities
- No hardcoded secrets

### Platform Security
- Minimum API level 26 (Android 8.0)
- Regular dependency updates
- Network security configuration
- Broadcast receiver permissions properly scoped
- Service permissions properly defined

## Supported Versions

| Version | Supported |
|---------|----------|
| 1.0.x   | ✅ Yes   |

## Security Updates

Security updates will be released as soon as possible after discovery and verification of vulnerabilities.

## Best Practices for Users

1. Keep the app updated
2. Use strong, unique API keys
3. Never share your API keys
4. Monitor app permissions in system settings
5. Use app in secure environments
6. Report suspicious activity

## Contact

Security Team: fizannawazgujjar@gmail.com
