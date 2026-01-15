# Software Requirements Specification - Movie Management

## MV-UC-01 - View Movie Information

| Field                 | Value                  |
| --------------------- | ---------------------- |
| **Use Case ID**       | MV-UC-01               |
| **Use Case Name**     | View Movie Information |
| **Created By**        | System Analyst         |
| **Last Updated By**   | System Analyst         |
| **Date Created**      | 15/01/2026             |
| **Date Last Updated** | 15/01/2026             |

### Actor

Admin, Manager

### Description

Allows the Admin/Manager to view the list of all movies in the system, including detailed information such as title, description, duration, director, cast members, release date, status, age rating, and genres. The system supports multiple views including all movies, now showing movies, coming soon movies, and archived movies.

### Preconditions

- The Admin/Manager has successfully logged into the system.

### Postconditions

- The list of movies is displayed completely and accurately with appropriate filtering.

### Priority

High

### Frequency of Use

Daily or whenever movie information needs to be checked.

### Normal Course of Events

**For Admin/Manager:**

1. The Admin/Manager accesses the "Movies" function.
2. The system displays the list of all movies with the following information:
   - Movie title
   - Poster image
   - Duration
   - Release date
   - Status (Now Showing, Coming Soon, Archived)
   - Age rating
   - Genres
3. The user can filter movies by:
   - Status (Now Showing, Coming Soon, Archived)
   - Genre
4. The system displays an archive warning indicator for "Now Showing" movies that have no scheduled screenings in the next 7 days.
5. The user can select a movie to view detailed information including:
   - Full description
   - Director
   - Cast members
   - Trailer URL
   - End date

### Alternative Courses

- **A1**: The user filters by "Now Showing" - the system displays only currently showing movies.
- **A2**: The user filters by "Coming Soon" - the system displays only upcoming movies.
- **A3**: The user filters by specific genre - the system displays only movies in that genre.

### Exceptions

- **E1**: If no movies are available, the system displays the message: "No movies found."
- **E2**: If a selected genre has no movies, the system displays: "No movies found for this genre."

### Includes

None

### Extends

- MV-UC-03: Edit Movie Information
- MV-UC-04: Delete Movie
- MV-UC-05: Archive Movie

### Special Requirements

- The movie list must load quickly with pagination support.
- Movie posters should be optimized for fast loading.
- Archive warning should be calculated based on screening schedule in real-time.

### Assumptions

- Each movie has a unique identifier (UUID).
- Movie status is automatically managed based on release and end dates.
- Managers have the same viewing permissions as Admin for movies.

### Notes and Issues

- The system automatically flags movies needing archival if no screenings are scheduled within 7 days.

---

## MV-UC-02 - Add New Movie

| Field                 | Value          |
| --------------------- | -------------- |
| **Use Case ID**       | MV-UC-02       |
| **Use Case Name**     | Add New Movie  |
| **Created By**        | System Analyst |
| **Last Updated By**   | System Analyst |
| **Date Created**      | 15/01/2026     |
| **Date Last Updated** | 15/01/2026     |

### Actor

Admin

### Description

Allows the Admin to add a new movie to the system, including all movie details such as title, description, duration, director, cast, release dates, age rating, genres, poster and trailer URLs.

### Preconditions

- The Admin has successfully logged into the system.
- At least one age rating exists in the system.
- At least one genre exists in the system.

### Postconditions

- The movie information is saved successfully with a unique identifier.
- The movie is displayed in the movie list with appropriate status.

### Priority

High

### Frequency of Use

When new movies are added to the cinema's catalog.

### Normal Course of Events

1. The Admin accesses the "Movies" function.
2. The Admin clicks the "Add New Movie" button.
3. The system displays the add movie form with the following fields:
   - **Title** (required)
   - **Description** (required)
   - **Duration in Minutes** (required)
   - **Director** (required)
   - **Cast Members** (required)
   - **Poster URL** (required)
   - **Trailer URL** (optional)
   - **Release Date** (required)
   - **End Date** (optional)
   - **Age Rating** (required, dropdown selection)
   - **Genres** (required, multiple selection)
   - **Status** (required, options: Now Showing, Coming Soon, Archived)
4. The Admin enters all required information.
5. The Admin selects appropriate age rating from the dropdown.
6. The Admin selects one or more genres.
7. The Admin clicks the "Save Movie" button.
8. The system validates the input data:
   - All required fields are filled
   - Age rating ID exists
   - All selected genre IDs exist
   - Duration is a positive number
   - Release date is valid
9. If valid, the system:
   - Generates a unique UUID for the movie
   - Saves the movie to the database
   - Establishes relationships with age rating and genres
   - Displays the message: "Movie added successfully"
10. The system redirects to the movie details page.

### Alternative Courses

- **A1**: The Admin clicks "Cancel" to exit the form without saving.
- **A2**: The Admin can preview the poster and trailer before saving.

### Exceptions

- **E1**: If required fields are missing, the system displays validation errors for each field: "Please fill out this field."
- **E2**: If the selected age rating does not exist, the system displays: "Invalid age rating selected."
- **E3**: If any selected genre does not exist, the system displays: "One or more selected genres are invalid."
- **E4**: If duration is not a positive number, the system displays: "Duration must be a positive number."
- **E5**: If release date is in invalid format, the system displays: "Invalid release date format."

### Includes

None

### Extends

None

### Special Requirements

- The system must validate all genre and age rating references before saving.
- Poster and trailer URLs should be validated for correct format.
- The system should support image preview for poster URLs.

### Assumptions

- Age ratings and genres are pre-configured in the system.
- Only Admin users can add new movies.
- Movie status can be manually set during creation.

### Notes and Issues

- Consider implementing auto-detection of movie status based on release date.
- Future enhancement: Integration with external movie databases for auto-fill.

---

## MV-UC-03 - Edit Movie Information

| Field                 | Value                  |
| --------------------- | ---------------------- |
| **Use Case ID**       | MV-UC-03               |
| **Use Case Name**     | Edit Movie Information |
| **Created By**        | System Analyst         |
| **Last Updated By**   | System Analyst         |
| **Date Created**      | 15/01/2026             |
| **Date Last Updated** | 15/01/2026             |

### Actor

Admin

### Description

Allows the Admin to update existing movie information, including title, description, duration, director, cast, dates, age rating, genres, and status. The system prevents archiving movies that have scheduled future screenings.

### Preconditions

- The Admin has successfully logged into the system.
- The movie to be edited already exists in the system.

### Postconditions

- The movie information is updated successfully in the database.
- All related screenings reflect the updated information.

### Priority

High

### Frequency of Use

Whenever movie information changes or needs correction.

### Normal Course of Events

1. The Admin accesses the movie list (MV-UC-01).
2. The Admin selects a movie and clicks the "Edit" button.
3. The system displays the edit movie form pre-filled with current information:
   - Title
   - Description
   - Duration in Minutes
   - Director
   - Cast Members
   - Poster URL
   - Trailer URL
   - Release Date
   - End Date
   - Age Rating (dropdown)
   - Genres (multiple selection)
   - Status (dropdown)
4. The Admin modifies the required fields.
5. The Admin clicks the "Save Changes" button.
6. The system validates the updated data:
   - Required fields are not empty
   - Age rating exists if changed
   - Selected genres exist if changed
   - Duration is positive if changed
   - Status change is valid
7. **Special validation for status change to "Archived":**
   - The system checks if the movie has scheduled screenings
   - If scheduled screenings exist, prevent archiving
8. If all validations pass, the system:
   - Updates the movie information
   - Updates relationships with age rating and genres if changed
   - Logs the update action
   - Displays the message: "Movie updated successfully"
9. The system refreshes the movie details page.

### Alternative Courses

- **A1**: The Admin clicks "Cancel" to exit without saving any changes.
- **A2**: The Admin can reset the form to original values before saving.

### Exceptions

- **E1**: If a required field is left empty, the system displays: "Please fill out this field."
- **E2**: If trying to change status to "Archived" and the movie has scheduled screenings, the system displays: "Cannot archive movie with scheduled screenings. Please cancel or complete all scheduled screenings first."
- **E3**: If the selected age rating does not exist, the system displays: "Invalid age rating selected."
- **E4**: If any selected genre does not exist, the system displays: "One or more selected genres are invalid."
- **E5**: If the movie ID is not found, the system displays: "Movie not found."

### Includes

None

### Extends

None

### Special Requirements

- The system must check for scheduled screenings before allowing status change to "Archived".
- All validations must be performed before any database updates.
- The system should maintain an audit trail of movie updates.

### Assumptions

- Movie information can be edited at any time by Admin.
- Editing movie information does not automatically affect existing bookings.

### Notes and Issues

- Consider implementing a warning system for significant changes (e.g., duration change affecting schedules).
- Future enhancement: Track change history for compliance and auditing.

---

## MV-UC-04 - Delete Movie

| Field                 | Value          |
| --------------------- | -------------- |
| **Use Case ID**       | MV-UC-04       |
| **Use Case Name**     | Delete Movie   |
| **Created By**        | System Analyst |
| **Last Updated By**   | System Analyst |
| **Date Created**      | 15/01/2026     |
| **Date Last Updated** | 15/01/2026     |

### Actor

Admin

### Description

Allows the Admin to permanently delete a movie from the system when the movie is no longer needed or was added by mistake. This is a destructive operation that should be used with caution.

### Preconditions

- The Admin has successfully logged into the system.
- The movie to be deleted exists in the system.

### Postconditions

- The movie is permanently removed from the database.
- All relationships with genres are removed.
- The movie no longer appears in any movie list.

### Priority

Medium

### Frequency of Use

Rarely - only when movies need to be permanently removed from the system.

### Normal Course of Events

1. The Admin accesses the movie list (MV-UC-01).
2. The Admin selects the movie to be deleted.
3. The Admin clicks the "Delete" button.
4. The system displays a confirmation dialog with:
   - Movie title
   - Warning message: "This action cannot be undone. Are you sure you want to permanently delete this movie?"
   - Confirmation options: "Delete" and "Cancel"
5. The Admin clicks "Delete" to confirm.
6. The system performs the deletion:
   - Removes the movie from the database
   - Removes all genre associations
   - Logs the deletion action
7. The system displays the message: "Movie deleted successfully."
8. The system refreshes the movie list without the deleted movie.

### Alternative Courses

- **A1**: The Admin selects "Cancel" in the confirmation dialog, and the system returns to the movie list without performing the deletion.
- **A2**: Instead of deleting, the Admin can choose to archive the movie (see MV-UC-05).

### Exceptions

- **E1**: If the movie ID is not found, the system displays: "Movie not found."
- **E2**: If the deletion fails due to database constraints, the system displays: "Cannot delete movie. Please ensure all related screenings and bookings are removed first."
- **E3**: If there's a system error during deletion, the system displays: "An error occurred while deleting the movie. Please try again later."

### Includes

None

### Extends

None

### Special Requirements

- The system must require explicit confirmation before deleting a movie.
- The deletion should be logged for audit purposes with timestamp and Admin user.
- Consider soft delete (archiving) as an alternative to permanent deletion.

### Assumptions

- Only Admin users are authorized to delete movies.
- Movies with active screenings or bookings should be archived instead of deleted.

### Notes and Issues

- **Important**: Consider implementing soft delete (archiving) instead of permanent deletion to maintain data integrity.
- Future enhancement: Implement a "trash bin" feature allowing recovery of deleted movies within a time period.
- Should restrict deletion if the movie has historical booking data.

---

## MV-UC-05 - Archive Movie

| Field                 | Value          |
| --------------------- | -------------- |
| **Use Case ID**       | MV-UC-05       |
| **Use Case Name**     | Archive Movie  |
| **Created By**        | System Analyst |
| **Last Updated By**   | System Analyst |
| **Date Created**      | 15/01/2026     |
| **Date Last Updated** | 15/01/2026     |

### Actor

Admin, Manager

### Description

Allows the Admin/Manager to archive a movie, changing its status to "Archived". This is a safer alternative to deletion, keeping the movie data in the system but removing it from active lists. The system provides warnings for movies that should be archived.

### Preconditions

- The Admin/Manager has successfully logged into the system.
- The movie to be archived exists in the system.
- The movie has no scheduled future screenings.

### Postconditions

- The movie status is changed to "Archived".
- The movie no longer appears in "Now Showing" or "Coming Soon" lists.
- The movie is still accessible through "Archived" filter and direct search.

### Priority

High

### Frequency of Use

When movies complete their theatrical run or are no longer showing.

### Normal Course of Events

**Scenario 1: Manual Archive**

1. The Admin/Manager accesses the movie list (MV-UC-01).
2. The Admin/Manager identifies a movie to archive.
3. The Admin/Manager clicks the "Archive" button.
4. The system validates that the movie has no scheduled screenings.
5. If validation passes, the system:
   - Changes the movie status to "Archived"
   - Saves the change to the database
   - Logs the archival action
   - Displays the message: "Movie archived successfully"
6. The system refreshes the movie list, moving the archived movie to the archived section.

**Scenario 2: Archive Warning System**

1. The system automatically checks "Now Showing" movies daily.
2. For each "Now Showing" movie, the system checks if there are scheduled screenings in the next 7 days.
3. If no screenings are scheduled within 7 days, the system sets a warning flag.
4. When Admin/Manager views the movie list, movies with warning flags display an archive warning indicator.
5. The Admin/Manager can click on the warning to view details and choose to archive.

### Alternative Courses

- **A1**: If the user decides not to archive, they click "Cancel" and the movie status remains unchanged.
- **A2**: The user can schedule new screenings instead of archiving.

### Exceptions

- **E1**: If the movie has scheduled future screenings, the system displays: "Cannot archive movie with scheduled screenings. The movie has upcoming scheduled screenings. Please cancel or complete all scheduled screenings before archiving."
- **E2**: If the movie is already archived, the system displays: "Movie is already archived."
- **E3**: If the movie ID is not found, the system displays: "Movie not found."

### Includes

None

### Extends

- Extended by: MV-UC-03 (Edit Movie Information - status change to archived)

### Special Requirements

- The system must automatically detect movies that should be archived based on screening schedule.
- Archive warnings should be calculated in real-time or updated at least daily.
- The archive operation must check for scheduled screenings before allowing archival.
- Archived movies should remain searchable and viewable but not appear in active listings.

### Assumptions

- Archiving is preferred over deletion to maintain historical records.
- Archived movies can be unarchived by changing their status back.
- Managers have permission to archive movies in their assigned cinemas.

### Notes and Issues

- The 7-day warning threshold is configurable and should be adjustable by system administrators.
- Consider implementing bulk archive functionality for multiple movies.
- Archived movies should still be accessible for reporting and historical analysis.

---

## MV-UC-06 - Search Movies

| Field                 | Value          |
| --------------------- | -------------- |
| **Use Case ID**       | MV-UC-06       |
| **Use Case Name**     | Search Movies  |
| **Created By**        | System Analyst |
| **Last Updated By**   | System Analyst |
| **Date Created**      | 15/01/2026     |
| **Date Last Updated** | 15/01/2026     |

### Actor

Admin, Manager, Customer (if implemented for public use)

### Description

Allows users to search for movies using various criteria including title search (partial match, case-insensitive), genre filtering, and status filtering. This provides flexible ways to find specific movies in the system.

### Preconditions

- The user has access to the movie search interface.

### Postconditions

- The system displays a filtered list of movies matching the search criteria.

### Priority

High

### Frequency of Use

Very frequent - used whenever users need to find specific movies.

### Normal Course of Events

**Scenario 1: Search by Title**

1. The user accesses the movie search interface.
2. The user enters a search term in the title search box.
3. The system performs a case-insensitive, partial match search.
4. The system displays all movies whose titles contain the search term.
5. Results are displayed with basic movie information (title, poster, duration, status).

**Scenario 2: Filter by Genre**

1. The user accesses the movie search interface.
2. The user selects a genre from the genre filter dropdown.
3. The system validates that the selected genre exists.
4. The system displays all movies associated with the selected genre.
5. Results show movies that have the selected genre among their genres.

**Scenario 3: Filter by Status**

1. The user accesses the movie search interface.
2. The user selects a status filter: "Now Showing", "Coming Soon", or "Archived".
3. The system displays all movies with the selected status.
4. For "Now Showing" - displays currently active movies.
5. For "Coming Soon" - displays upcoming movies.
6. For "Archived" - displays archived movies.

**Scenario 4: Combined Search**

1. The user can combine multiple search criteria (title + genre, title + status, etc.).
2. The system applies all filters and returns movies matching all criteria.

### Alternative Courses

- **A1**: If the search term is empty, the system displays all movies (same as MV-UC-01).
- **A2**: The user can clear all filters to return to the full movie list.
- **A3**: The user can sort results by various criteria (title, release date, etc.).

### Exceptions

- **E1**: If no movies match the search criteria, the system displays: "No movies found matching your search criteria."
- **E2**: If the selected genre does not exist, the system displays: "Invalid genre selected."
- **E3**: If an invalid status is provided, the system displays: "Invalid status filter."

### Includes

None

### Extends

None

### Special Requirements

- Search should be fast and responsive, with results appearing as the user types (debounced).
- Search should be case-insensitive and support partial matches.
- The system should highlight matching text in search results.
- Search should support special characters in movie titles.

### Assumptions

- Search functionality is available to all user roles.
- Search performance is acceptable with the current database size.
- Partial matching is sufficient for title search.

### Notes and Issues

- Consider implementing advanced search with multiple simultaneous filters.
- Future enhancement: Full-text search across description and cast members.
- Consider adding search history and suggestions.
- May need to implement pagination for large result sets.

---

## Data Dictionary

### Movie Entity Attributes

| Attribute       | Type          | Description                                    | Constraints                                          |
| --------------- | ------------- | ---------------------------------------------- | ---------------------------------------------------- |
| id              | String (UUID) | Unique identifier for the movie                | Primary Key, Auto-generated                          |
| title           | String        | The title of the movie                         | Required, Max length: 255                            |
| description     | String        | Detailed description of the movie              | Required                                             |
| durationMinutes | Integer       | Duration of the movie in minutes               | Required, Must be positive                           |
| director        | String        | Name of the movie director                     | Required                                             |
| castMembers     | String        | Comma-separated list of main cast members      | Required                                             |
| posterUrl       | String        | URL to the movie poster image                  | Required, Valid URL format                           |
| trailerUrl      | String        | URL to the movie trailer video                 | Optional, Valid URL format                           |
| releaseDate     | LocalDate     | Date when the movie is released                | Required                                             |
| endDate         | LocalDate     | Date when the movie stops showing              | Optional                                             |
| ageRatingId     | String        | Foreign key to age rating                      | Required, Must exist in age_ratings table            |
| status          | Enum          | Current status of the movie                    | Required, Values: now_showing, coming_soon, archived |
| genres          | Set<Genre>    | Collection of genres associated with the movie | Required, At least one genre                         |

### Movie Status Enumeration

| Value       | Description                            |
| ----------- | -------------------------------------- |
| now_showing | Movie is currently showing in theaters |
| coming_soon | Movie is scheduled for future release  |
| archived    | Movie is no longer actively showing    |

---

## Business Rules

### BR-01: Movie Status Management

- A movie can only be in one status at a time: now_showing, coming_soon, or archived.
- Movies with scheduled future screenings cannot be archived.

### BR-02: Archive Warning System

- Movies with "now_showing" status are automatically flagged for archival if they have no screenings scheduled within the next 7 days.
- The 7-day threshold is configurable.

### BR-03: Genre Association

- Every movie must be associated with at least one genre.
- A movie can be associated with multiple genres.
- Deleting a genre should not delete associated movies.

### BR-04: Age Rating Requirements

- Every movie must have an age rating.
- Age ratings must exist in the system before creating a movie.

### BR-05: Deletion vs. Archiving

- Archiving is the preferred method for removing movies from active listings.
- Permanent deletion should only be used for movies added in error.
- Movies with historical booking data should never be permanently deleted.

### BR-06: Search Functionality

- Title search is case-insensitive and supports partial matching.
- Movies can be searched by title, filtered by genre, or filtered by status.
- Search results should maintain consistent ordering.

---

## API Endpoints Summary

| Method | Endpoint                     | Description            | Use Case           |
| ------ | ---------------------------- | ---------------------- | ------------------ |
| GET    | /movies                      | Get all movies         | MV-UC-01           |
| GET    | /movies/{id}                 | Get movie by ID        | MV-UC-01           |
| GET    | /movies/status/{status}      | Get movies by status   | MV-UC-01, MV-UC-06 |
| GET    | /movies/now-showing          | Get now showing movies | MV-UC-01, MV-UC-06 |
| GET    | /movies/coming-soon          | Get coming soon movies | MV-UC-01, MV-UC-06 |
| GET    | /movies/search?title={title} | Search movies by title | MV-UC-06           |
| GET    | /movies/genre/{genreId}      | Get movies by genre    | MV-UC-06           |
| POST   | /movies                      | Create new movie       | MV-UC-02           |
| PUT    | /movies/{id}                 | Update movie           | MV-UC-03           |
| PATCH  | /movies/{id}/archive         | Archive movie          | MV-UC-05           |
| DELETE | /movies/{id}                 | Delete movie           | MV-UC-04           |

---

## Non-Functional Requirements

### Performance Requirements

- **NFR-01**: The movie list page should load within 2 seconds for up to 1000 movies.
- **NFR-02**: Search results should appear within 1 second of user input.
- **NFR-03**: Movie poster images should be cached and optimized for fast loading.

### Security Requirements

- **NFR-04**: Only authenticated Admin users can create, update, or delete movies.
- **NFR-05**: Managers can view and archive movies but cannot delete them.
- **NFR-06**: All movie management actions should be logged with user and timestamp.

### Usability Requirements

- **NFR-07**: The movie management interface should be intuitive and require minimal training.
- **NFR-08**: Error messages should be clear and guide users toward resolution.
- **NFR-09**: Confirmation dialogs should be used for destructive operations.

### Reliability Requirements

- **NFR-10**: The system should validate all data before saving to prevent inconsistent states.
- **NFR-11**: Database transactions should be atomic to prevent partial updates.

### Maintainability Requirements

- **NFR-12**: The movie management code should follow established coding standards.
- **NFR-13**: All movie operations should be properly logged for debugging and auditing.

---

## Glossary

| Term         | Definition                                                                            |
| ------------ | ------------------------------------------------------------------------------------- |
| Movie        | A film that is shown or can be shown in the cinema                                    |
| Archive      | To move a movie to inactive status while retaining its data                           |
| Genre        | A category of movie characterized by similarities in style, theme, or subject matter  |
| Age Rating   | A classification assigned to a movie indicating the appropriate age group for viewing |
| Now Showing  | Movies currently being screened in the cinema                                         |
| Coming Soon  | Movies scheduled for future release                                                   |
| Screening    | A showing of a movie at a specific time and location                                  |
| Cast Members | The actors and actresses who perform in the movie                                     |
| UUID         | Universally Unique Identifier - a 128-bit identifier used as primary key              |

---

## Appendices

### Appendix A: Validation Rules

#### Movie Creation/Update Validations

1. **Title**: Required, non-empty, max 255 characters
2. **Description**: Required, non-empty
3. **Duration**: Required, must be a positive integer
4. **Director**: Required, non-empty
5. **Cast Members**: Required, non-empty
6. **Poster URL**: Required, must be valid URL format
7. **Trailer URL**: Optional, must be valid URL format if provided
8. **Release Date**: Required, valid date format
9. **End Date**: Optional, must be after release date if provided
10. **Age Rating**: Required, must exist in database
11. **Genres**: Required, at least one genre, all must exist in database
12. **Status**: Required, must be one of: now_showing, coming_soon, archived

#### Archive Operation Validations

1. Movie must exist
2. Movie must not have scheduled future screenings
3. Status will be changed to "archived"

### Appendix B: Error Codes

| Error Code                     | Description                                 | User Message                                      |
| ------------------------------ | ------------------------------------------- | ------------------------------------------------- |
| MOVIE_NOT_EXISTED              | Movie ID not found                          | "Movie not found."                                |
| AGERATING_NOT_EXISTED          | Age rating ID not found                     | "Invalid age rating selected."                    |
| GENRE_NOT_EXISTED              | Genre ID not found                          | "One or more selected genres are invalid."        |
| MOVIE_HAS_SCHEDULED_SCREENINGS | Cannot archive movie with future screenings | "Cannot archive movie with scheduled screenings." |

### Appendix C: Future Enhancements

1. **Bulk Operations**: Allow archiving or updating multiple movies at once
2. **Advanced Search**: Full-text search across all movie fields including description and cast
3. **Movie Analytics**: View statistics on movie performance and popularity
4. **Automatic Status Updates**: Auto-update status based on release and end dates
5. **Integration with External APIs**: Auto-populate movie data from IMDB or TMDB
6. **Movie Recommendations**: Suggest similar movies based on genre and ratings
7. **Trailer Preview**: Embedded trailer player in movie details
8. **Version History**: Track and display all changes made to a movie
9. **Soft Delete/Restore**: Implement trash bin functionality for deleted movies
10. **Export Functionality**: Export movie catalog to CSV/PDF formats

---

**Document Version**: 1.0  
**Last Updated**: 15/01/2026  
**Status**: Draft  
**Approval Status**: Pending Review
