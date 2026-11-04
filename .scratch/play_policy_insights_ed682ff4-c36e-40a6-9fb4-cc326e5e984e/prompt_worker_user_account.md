


## User Account and Identity Audit

### Hint: Deducing Account Presence

To determine if an app handles user accounts (even if it uses third-party
providers like Google Sign-In or Firebase Auth), look for:
1. **Login/Auth Screens**: Semantic files or layouts named `login`, `auth`,
   `signin`, or `signup`.
2. **Account Management**: APIs like `AccountManager`, `CredentialManager`, or
   `Firebase.auth`.
3. **User Profile**: UI strings or data models referencing `profile`,
   `my account`, or `user settings`.

### Policies to Verify

#### Play Console Requirements (Policy ID: login_credentials)

- **Goal**: Identify if the app implements a login wall or authentication
  screen.
- **The Policy Spirit**: To ensure Play Store reviewers can successfully test
  and audit apps, developers must submit functional, non-expiring credentials in
  Play Console if the app's features are gated behind a login screen.
- **Evidence**:
  
  
  - **Account Signals**:
    `app\src\main\kotlin\n7\ad2\di\ApplicationComponent.kt (Pattern: uid),
app\src\main\kotlin\n7\ad2\di\ComponentDependenciesModule.kt (Pattern: uid),
app\src\main\res\layout\item_list_guide_item.xml (Pattern: uid),`
  
- **Common Evaluation Matrix**:
  | App State | Finding / Condition | Severity | Actionable Recommendation |
  | :--- | :--- | :--- | :--- |
  | **Login Screen Detected** | App displays or contains a login screen, registration wall, or authentication interface. | `IMPORTANT` | **Administrative Console Requirements**: Because your app implements a login flow, you MUST complete two manual setup steps in the Play Console dashboard to pass review:<br>1. **Reviewer Demo Credentials**: Submit active, non-expiring test credentials so Google Play reviewers can access your gated features.<br>2. **Account Deletion Link**: Submit a public-facing web link for account deletion to satisfy Google Play's data deletion policies. |

- **Domain-Specific Heuristics (Strictly Bounded)**:
  Look beyond standard native login buttons. Extrapolate using this heuristic:
  1. **Hidden Gatekeepers**: Analyze if certain critical features (e.g.,
     synchronizing local database, checkout forms, or member-only dashboards)
     require authentication even if the app opens directly to a main page. If
     you deduce that functional workflows require a login, treat it as a login
     gate and output the `IMPORTANT` console credentials and deletion link
     reminders.

#### Account Deletion Requirement (Policy ID: account_deletion)

- **Goal**: If the app handles user accounts, it must provide a discoverable
  in-app account deletion mechanism.
- **The Policy Spirit**: Users have a fundamental right to request data erasure.
  If they can create an account in-app, they must be able to delete it in-app.
  Deletion must wipe remote database records, not just sign out.
- **Evidence**:
  
  - **Account signals (Presence)**:
    `app\src\main\kotlin\n7\ad2\di\ApplicationComponent.kt (Pattern: uid),
app\src\main\kotlin\n7\ad2\di\ComponentDependenciesModule.kt (Pattern: uid),
app\src\main\res\layout\item_list_guide_item.xml (Pattern: uid),`
  
  
  - **Deletion signals (Mechanism)**:
    `app\src\main\res\values-ru\strings.xml (Pattern: deactivate),
app\src\main\res\values\strings.xml (Pattern: deactivate),`
  
- **Common Evaluation Matrix**:
  | App Account Status | Finding / Deletion Evidence | Severity | Actionable Recommendation |
  | :--- | :--- | :--- | :--- |
  | **Handles User Accounts** | App manages user accounts, but NO code, layout, or string suggests an in-app deletion button or process. | `IMPORTANT` | **Implement in-app deletion**: Create a highly discoverable path (e.g., under User Profile/Account Settings) to let users initiate account deletion directly in the app. |

- **Domain-Specific Heuristics (Strictly Bounded)**:
  Do not limit your analysis to basic "Delete" buttons. Actively analyze custom
  and grey-area implementations:
  1. **The Partial Deletion Trap**: Read the implementation of any found
     deletion mechanisms. If the code merely calls `clearPreferences()`, clears
     a local cookie, or triggers a standard `logout()` without sending a remote
     delete/purge network API call to clean up backend user records, flag this
     as a `IMPORTANT` violation of the deletion mandate.
  2. **Indirect User Accounts**: If the app uses third-party sign-in bridges
     (e.g., Google Sign-In, Firebase) but does not store an
     explicit account profile on its own server, it still handles user account
     details if any user preferences or device identifiers are cached remotely.
     If so, a delete link/button is still required.

## Output schema

Save final JSON output to `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e/worker_{{GOAL_NAME}}.json`.

```json
{
  "domain": "User Account and Identity",
  "findings": [
    {
      "policy_id": "STRING_VALUE (The exact Policy ID, e.g., account_deletion)",
      "issue_summary": "STRING_VALUE",
      "severity": "CRITICAL | IMPORTANT | SUGGESTION",
      "files_involved": ["STRING_VALUE"],
      "evidence": "STRING_VALUE",
      "recommendation": "STRING_VALUE"
    }
  ]
}
```

# Execution Mandates

### Technical Rules

1.  **Absolute Paths Only**: Always resolve and use absolute paths.
2.  **Containment**: Write all artifacts strictly within `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e`.
3.  **Fail-fast**: If any required input file is missing, stop immediately and
    report the failure.

### Surgical Input Protocol & Efficient Search (MANDATORY)

-   **Direct Evidence First**: Prioritize files listed in the **Context &
    Evidence** sections. Use the provided file/line evidence (e.g., from Data
    Sources or Sinks) to jump directly to the relevant code. Do not perform
    broad workspace searches if these surgical starting points are available.
-   **Path Filtering Over File Crawling**: Locate target files by name, path, or
    extension *first* using directory/file listing tools before performing any
    text/content-based searches. Restrict searches and file reads strictly to
    the target `D:\AndroidProject\AD2`.
-   **Strict Exclusions (The Noise Wall)**: Configure search, glob, and find
    tools to ignore build, cache, dependencies, and testing folders. You MUST
    exclude matches from: `**/build/**`, `**/.gradle/**`, `**/.scratch/**`,
    `**/androidTest/**`, `**/test/**`, `**/node_modules/**`.
-   **Targeted Extensions**: Restrict content searches and file reads strictly
    to source and configuration files: `.java`, `.kt`, `.xml`, `.gradle`, `.kts`
    (and `.js`, `.ts`, `.jsx`, `.tsx`, `.dart` if a hybrid/cross-platform
    environment is analyzed). Never search or read inside compiled `.class`
    files, binary resources, or output assets.
-   **Surgical Queries & Limiters**: Use highly specific search patterns (e.g.,
    search for `getLastKnownLocation` or `deleteAccount` instead of general
    words like `location` or `delete`). If search tools support limits or
    pagination, cap results at a maximum of 50 matches. Do not load unlimited
    search results into your context window.
-   **Parallel Reading Required (Turn Efficiency)**: You are operating under a
    strict maximum turn limit. To prevent timeouts, you MUST request to read
    multiple target files concurrently in a single response. Do not read the
    evidence files sequentially one-by-one. Issue all of your file-reading tool
    calls simultaneously whenever possible.

### Evidentiary Standard & Guardrails (CRITICAL)

To prevent over-auditing, false positives, and speculative "prosecution" of
compliant code during extrapolation:

1.  **Presumption of Compliance**: Treat code as compliant unless there is
    *definitive, visible evidence* in the provided files of a policy violation.
    If code is ambiguous, or if network/database logic is hidden behind
    abstractions (e.g., calling an interface or repository method like
    `clearSession()`), you must assume standard compliant behavior. Do NOT guess
    or speculate about what happens behind interfaces.
2.  **Benefit of the Doubt**: When compliance cannot be strictly verified due to
    code abstractions or missing source file contexts, you must downgrade your
    finding:
    -   Never flag a `🔴 Critical` or `🟡 Important` finding based on suspicion or
        lack of context.
    -   Instead, output a `🔵 Suggestion` (informational) to advise the developer
        on what to double-check in their backend or configuration.
3.  **Exclusion of Local State**: Local-only processing (e.g., caching theme
    settings, user-selected visual configurations, or on-device-only database
    operations) is explicitly exempt from Data Safety collection or Account
    Deletion mandates.
4.  **Concrete Attributions**: Every `🔴 Critical` or `🟡 Important` finding must
    cite the exact file, line number, or configuration block containing the
    direct violation. If you cannot cite the exact line of code containing the
    violation, you cannot flag it as a violation.
5.  **Empty-List Discipline**: If no policy violations, discrepancies, or review
    items are identified during your audit, you MUST represent this as an empty
    array `[]` for that field (e.g., `"findings": []`, `"verified_findings":
    []`, or `"manual_verification_required": []`). **DO NOT** populate arrays
    with "dummy" objects, placeholder strings, or `"N/A"` / `"None"` values.
6.  **Heuristics & Extrapolation Boundaries**: Whenever applying specific
    heuristics defined in your goal (e.g., searching for implicit logger leaks
    or
    SDK siphoning), you must strictly bound them to the provided evidence and
    their immediate callers. You are strictly forbidden from initiating broad,
    unbounded searches for custom paths or variables across the wider codebase.
    Base your extrapolation only within the specific files already provided to
    you in the prompt.

### Finalization & Output Mandates (CRITICAL)

-   **Iterative Saving**: If your investigation requires multiple steps, save
    partial or intermediate JSON states to disk as you progress. Do not hold all
    data in memory until the very end to prevent data loss upon interruption.
-   **Strict File Output (NO TRIPLE BACKTICKS)**: You MUST save your final JSON
    output to disk at the exact path specified in the goal schema using your
    file-writing capabilities.
    -   **CRITICAL: The content written to the file MUST be pure, raw JSON. DO
        NOT wrap the contents inside the JSON file with Markdown code blocks
        (such as triple backticks `json ...`). Writing markdown blocks into the
        file makes the JSON unparseable by the compiler.**
-   **NO Chat Summaries**: **MANDATORY: DO NOT summarize your findings, explain
    your reasoning, or output JSON in your final chat response.** Your chat
    output wastes context and is ignored by the orchestrator.
-   **Verification Before Termination**: You MUST only terminate and return the
    "SUCCESS" string *after* you have explicitly verified that your JSON file
    successfully wrote to disk and contains valid JSON (e.g., by reading the
    file back or checking the directory contents).
-   **Final response**: Your final response MUST be exactly the word: "SUCCESS"
    and nothing else.
