# Review of specialized policy findings (Chunked Critic)

Review a specific chunk of identified policy findings to identify potential
false positives, exaggerated claims, or inaccuracies due to lack of evidence.

### Provided context files

The prompt provides absolute paths to files and directories:
- `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e`: The temporary scratch directory for this audit.
- `input_file`: `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e/input_critic_1.json` contains the
  chunk of findings to verify.

### Instructions

1.  **Read input chunk**: Read the JSON object in
   `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e/input_critic_1.json`. The keys (e.g. `"1"`,
   `"2"`, `"3"`) correspond to the finding IDs.
2.  **Verify each finding**:
    -   For each finding, analyze its evidence against the source files in
      `D:\AndroidProject\AD2`.
    -   Determine the verdict:
        -   `"VERIFIED"`: True Positive. The codebase confirms the policy
          violation.
        -   `"MANUAL_REVIEW"`: Ambiguous code or abstract logic where automatic
          verification is impossible.
        -   `"PRUNED"`: False Positive. The codebase is compliant, or the
          finding is not supported by actual evidence.
    -   Provide your verification details in the output JSON.
3.  **Optional Editorial Overrides**: If (and only if) you need to edit, refine,
   or moderate the worker finding's text, you may include one or more of these
   optional keys to your decision object. **If you agree with the worker's text,
   you MUST omit these keys entirely from your JSON.**
    -   `"issue_summary"`: Write a more accurate, tailored summary.
    -   `"severity"`: Set to `"CRITICAL"`, `"IMPORTANT"`, or `"SUGGESTION"` to
      override.
    -   `"recommendation"`: Write a tailored, codebase-specific remediation
      step.
4.  **Save Results**: Save your final "Thin JSON" mapping to
   `D:\AndroidProject\AD2\.scratch\play_policy_insights_ed682ff4-c36e-40a6-9fb4-cc326e5e984e/critic_output_1.json`.

### Audit principles for false positive detection

-   **Environment detection**: **Do not** flag emulator or root detection as
  violations unless evidence shows malicious intent or review evasion.
-   **Speculative collection**: Prune claims based solely on permission
  presence. Require evidence of actual data access and transfer (whether
  off-device network egress or on-device sharing to a third-party app).
-   **Standard patterns**: **Do not** flag standard Android architectural
  patterns unless used maliciously.
-   **Surgical Evidence Standard**: Prune or downgrade any finding where the
  worker has speculated on a transmission pathway that cannot be directly
  verified in the immediate source files, or where the finding fails to cite a
  concrete file and line number containing the direct policy violation.
-   **Data Safety Flag Verification**: If a finding includes Data Safety flags
  (e.g., `user_initiated`, `is_third_party`), strictly verify them. If the
  worker claims `user_initiated: true`, ensure there is undeniable evidence of
  explicit user interaction triggering the transfer. If the worker claims
  `is_third_party: true`, verify the sink is definitively outside the
  developer's control (e.g., Android Share Sheet, Social Media SDK). If evidence
  is lacking, downgrade the finding or use `"issue_summary"` to correct the
  claim.
-   **Gatekeeper Validation**: If a worker claims a finding is compliant because
  a disclosure exists (`prominent_disclosure_status: "DISCLOSED"`), you MUST
  verify that the UI acts as a strict gatekeeper. If data collection begins
  before the user taps "Accept", or if they can dismiss it and continue,
  override the `"severity"` to `"CRITICAL"` and explicitly state the gatekeeper
  is invalid.

### Output JSON format

**Important**: The output must be a pure JSON object mapping the sequential
finding IDs from your input file to their decisions. If there are no findings in
the chunk, return `{}`.

```json
{
  "1": {
    "action": "VERIFIED | MANUAL_REVIEW | PRUNED",
    "confidence": "High | Medium | Low",
    "critic_justification": "A concise, technical explanation of your decision based on codebase evidence.",
    "issue_summary": "OPTIONAL: Overridden issue summary text",
    "severity": "OPTIONAL: CRITICAL | IMPORTANT | SUGGESTION",
    "recommendation": "OPTIONAL: Overridden recommendation text"
  }
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
