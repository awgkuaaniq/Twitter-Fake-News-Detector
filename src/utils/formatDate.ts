export const formatDate = (dateString: string | Date): string => {
  // Ensure the date string has 'Z' (indicating UTC) if it doesn't already
  const fixedDateString =
    typeof dateString === "string" && !dateString.endsWith("Z")
      ? `${dateString}Z`
      : dateString;

  // Create a Date object from the string (now correctly interpreted as UTC)
  const date = new Date(fixedDateString);
  const now = new Date();

  // Calculate the difference in milliseconds between now and the article's published date
  const diffMs = now.getTime() - date.getTime();

  // Time difference in minutes and hours
  const diffMinutes = Math.floor(diffMs / 60000); // 1 min = 60,000 ms
  const diffHours = Math.floor(diffMinutes / 60);

  if (diffMinutes < 60) {
    return `${diffMinutes} min${diffMinutes === 1 ? "" : "s"} ago`;
  } else if (diffHours < 24) {
    return `${diffHours} hour${diffHours === 1 ? "" : "s"} ago`;
  } else {
    // Format the date as "3 Nov 2024"
    const options: Intl.DateTimeFormatOptions = {
      day: "numeric",
      month: "short",
      year: "numeric",
    };
    return date.toLocaleDateString("en-GB", options); // For example: "3 Nov 2024"
  }
};
