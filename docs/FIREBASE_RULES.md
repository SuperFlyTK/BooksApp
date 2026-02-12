# Firebase Rules Assumptions

The app expects authenticated, user-scoped writes.

## Realtime Database structure

- `/users/{uid}/favorites/{bookId}`
- `/comments/{bookId}/{commentId}`

## Example rules (reference)

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid"
      }
    },
    "comments": {
      "$bookId": {
        "$commentId": {
          ".read": "auth != null",
          ".write": "auth != null && (!data.exists() || data.child('userId').val() == auth.uid)"
        }
      }
    }
  }
}
```

Notes:
- Client code also validates/sanitizes inputs before writing.
- Final rule tuning (e.g., schema checks) should be applied in Firebase Console for production.
