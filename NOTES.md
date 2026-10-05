# Patch Notes

## Summary of changes

I focused on a small set of high-value issues across the SQL, backend, and frontend layers.

1. Fixed the SQL operator-precedence bug in the Spring Data repository and Oracle reference query. The original query could apply archived and status filters incorrectly because AND has higher precedence than OR.

2. Removed the artificial Thread.sleep() delay from the backend search endpoint. The delay unnecessarily blocked API requests based on search-term length.

3. Added validation for pagination parameters and invalid status values to prevent bad input from causing runtime errors.

4. Reset pagination to page 1 whenever the search query or status filter changes.

5. Improved frontend loading and error-state handling so failed requests do not leave the UI stuck in a loading state.

## What I chose not to change

I did not rewrite the repository to use database-level Pageable pagination because that would make the patch larger than necessary for the 90-minute exercise. I also did not add search debouncing because it is an optimization rather than a critical correctness issue.

## Biggest remaining risk

The backend currently retrieves all matching records and performs pagination in Java. This could become inefficient with a large production dataset.

## Tools/AI used

I used AI assistance to inspect the frontend, backend, H2 SQL, and Oracle reference SQL, identify bugs, and reason about their root causes. I reviewed the suggested changes and kept the final patch focused on correctness and maintainability.