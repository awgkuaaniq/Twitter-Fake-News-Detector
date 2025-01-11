"use client";

import { useEffect, useState, Suspense, lazy } from "react";
import axios from "axios";

interface CrosscheckResult {
  title: string;
  content: string;
  source: string;
  probability: number;
}

interface Tweets {
  id: string;
  name: string;
  username: string;
  avatar: string;
  body: string;
  tweetUrl: string;
  article_url?: string;
  publishedAt: Date;
  crosscheck: CrosscheckResult;
}

const Tweet = lazy(() => import("@/components/Tweet"));

export default function Home() {
  const [tweets, setTweets] = useState<Tweets[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchTweets = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8000/api/tweets`);
      setTweets(response.data);
    } catch (error) {
      console.error("Error fetching tweets:", error);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchTweets();
  }, []); // Only fetch once when component mounts

  return (
    <main className="bg-gray-100 dark:bg-offblack min-h-screen">
      <div className="flex flex-col divide-y divide-black/15 dark:divide-offgray mx-auto px-2 max-w-7xl py-11">
        <Suspense fallback={<div>Loading tweets...</div>}>
          {tweets.map((tweet) => (
            <Tweet key={tweet.id} tweet={tweet} />
          ))}
        </Suspense>
        {loading && <div>Loading tweets...</div>}
      </div>
    </main>
  );
}
