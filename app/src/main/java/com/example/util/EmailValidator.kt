package com.example.util

data class EmailValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val suggestedEmail: String? = null,
    val isGmailTypo: Boolean = false
)

object EmailValidator {
    /**
     * Common misspellings of "gmail.com" frequently entered by users.
     */
    private val COMMON_GMAIL_TYPOS = listOf(
        "gmai.com", "gmaill.com", "gmial.com", "gamil.com", "gmal.com",
        "gmeil.com", "gmaik.com", "gmqil.com", "gmaul.com", "gmail.co",
        "gmail.con", "gmail.cm", "gmail.cpm", "gmail.om", "gmail.comm",
        "gmailcom", "gmail", "g-mail.com", "g.mail.com", "googlemail.con",
        "googlemial.com", "gmai.co", "gmai.con", "gma.com", "gmil.com",
        "gmaildotcom", "gmail.net", "gmail.org"
    )

    private val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    /**
     * Identifies if the user is typing an address with a typo in the Gmail domain.
     * Returns the suggested corrected email, or null if no Gmail typo detected.
     */
    fun checkGmailTypo(email: String): String? {
        val trimmed = email.trim()
        if (!trimmed.contains("@")) return null
        val parts = trimmed.split("@")
        if (parts.size != 2) return null
        val username = parts[0].trim()
        val domain = parts[1].trim().lowercase()

        if (domain.isNotEmpty() && domain != "gmail.com" && COMMON_GMAIL_TYPOS.contains(domain)) {
            return "$username@gmail.com"
        }
        return null
    }

    /**
     * Validates email syntax, domain structure, and specific Gmail formatting.
     */
    fun validateEmail(input: String): EmailValidationResult {
        val email = input.trim()
        if (email.isEmpty()) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Please enter your Gmail or registered email address."
            )
        }

        if (email.contains(" ")) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Email address cannot contain spaces."
            )
        }

        if (!email.contains("@")) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Missing '@' symbol. A valid Gmail address must be formatted: username@gmail.com"
            )
        }

        val parts = email.split("@")
        if (parts.size > 2) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Invalid email format: Multiple '@' symbols detected."
            )
        }

        val username = parts[0].trim()
        val domain = parts[1].trim().lowercase()

        if (username.isEmpty()) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Missing username before '@'. Example: creator@gmail.com"
            )
        }

        if (domain.isEmpty()) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Missing domain after '@'. Example: @gmail.com"
            )
        }

        // Check for common Gmail domain typos
        if (COMMON_GMAIL_TYPOS.contains(domain)) {
            val suggested = "$username@gmail.com"
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Wrong Gmail domain: You typed '@$domain'. Did you mean '@gmail.com'?",
                suggestedEmail = suggested,
                isGmailTypo = true
            )
        }

        if (!domain.contains(".")) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Incomplete domain extension. Missing '.com' (e.g. @gmail.com)."
            )
        }

        val domainParts = domain.split(".")
        val tld = domainParts.lastOrNull() ?: ""
        if (tld.length < 2) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Invalid domain extension '.$tld'. Valid extensions: .com, .io, .net, etc."
            )
        }

        if (!EMAIL_REGEX.matches(email)) {
            return EmailValidationResult(
                isValid = false,
                errorMessage = "Invalid email format. Please check for special characters or syntax errors."
            )
        }

        return EmailValidationResult(isValid = true)
    }
}
