"use client";

import { Suspense, useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Input } from "@/components/ui/input";
import Tweet from "@/components/Tweet";

interface CrosscheckResult {
  title: string;
  content: string;
  source: string;
  probability: number;
}

interface Tweet {
  id: string;
  name: string;
  username: string;
  avatar: string;
  body: string;
  tweetUrl: string;
  articleUrl?: string;
  publishedAt: Date;
  crosscheck: CrosscheckResult;
}

// Child component that uses useSearchParams
function SearchContent() {
  const searchParams = useSearchParams();
  const q = searchParams.get("q"); // Extract the search query
  const router = useRouter();
  const [searchInput, setSearchInput] = useState(q || "");
  const [results, setResults] = useState<Tweet[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (q) {
      const fetchResults = async () => {
        setLoading(true);
        setError(null);
        try {
          const response = await fetch(
            `http://localhost:8000/api/tweets/search?query=${encodeURIComponent(
              q
            )}`
          );
          if (!response.ok) throw new Error("Failed to fetch search results");

          const data: Tweet[] = await response.json();
          setResults(data);
        } catch (err: any) {
          setError(err.message);
        } finally {
          setLoading(false);
        }
      };

      fetchResults();
    }
  }, [q]); // Trigger search when query changes

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchInput(e.target.value);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && searchInput.trim() !== "") {
      router.push(`/search?q=${encodeURIComponent(searchInput)}`);
    }
  };

  return (
    <main className="bg-gray-100 min-h-screen pb-10 dark:bg-offblack">
      {/* Search Bar */}
      <div className="flex justify-center mx-auto px-2 max-w-7xl py-11">
        <Input
          className="border-black/25 w-full dark:border-white/10 dark:bg-offgray text-sm placeholder-gray-700 dark:placeholder-gray-400"
          type="text"
          id="search"
          value={searchInput}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          placeholder="Search tweets..."
        />
      </div>
      {/* Results */}
      <div className="flex divide-y divide-black/15 flex-col max-w-7xl mx-auto px-2">
        {loading && <div>Loading...</div>}
        {error && <div style={{ color: "red" }}>{error}</div>}
        {results.length > 0 ? (
          results.map((tweet) => <Tweet key={tweet.id} tweet={tweet} />)
        ) : (
          <div className="flex flex-col justify-items-center mx-auto items-center text-xl space-y-5">
            <p>
              Sorry, there are no tweets containing{" "}
              <span className="flex-inline font-bold">&apos;{q}&apos;</span>
            </p>
            <p className="text-2xl font-bold pt-4">Suggestions</p>
            <ul className="list-disc">
              <li>Make sure all words are spelled correctly</li>
              <li>Try different search terms</li>
              <li>Try more general keywords</li>
            </ul>
          </div>
        )}
      </div>
    </main>
  );
}

// Parent component that wraps SearchContent in Suspense
export default function SearchResult() {
  return (
    <Suspense fallback={<div>Loading search results...</div>}>
      <SearchContent />
    </Suspense>
  );
}
