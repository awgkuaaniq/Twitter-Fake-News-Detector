"use client";

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import axios from "axios";
import React, { useEffect, useState } from "react";

interface Statistics {
  mostActiveUser: {
    username: string;
    tweetCount: number;
  };
  todayTweetCount: number;
}

export default function StatisticsPage() {

    const [statistics, setStatistics] = useState<Statistics>({
      mostActiveUser: { username: "", tweetCount: 0 },
      todayTweetCount: 0,
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      const fetchStatistics = async () => {
        try {
          // Get most active users
          const usersResponse = await axios.get(
            "http://localhost:8000/api/tweets/statistics/most-active-users"
          );
          const users = usersResponse.data;
          const [topUser, count] = Object.entries(users)[0] || ["", 0];

          // Get today's tweet count
          const countResponse = await axios.get(
            "http://localhost:8000/api/tweets/statistics/today-count"
          );

          setStatistics({
            mostActiveUser: {
              username: topUser as string,
              tweetCount: count as number,
            },
            todayTweetCount: countResponse.data,
          });
        } catch (error) {
          console.error("Error fetching statistics:", error);
        } finally {
          setLoading(false);
        }
      };

      fetchStatistics();
    }, []);

    if (loading) {
      return <div>Loading statistics...</div>;
    }

  return (
    <main className="bg-gray-100 min-h-screen">
      {/* Main Container */}
      <div className="flex mx-auto px-2 justify-between max-w-7xl py-11">
        {/* Number of fake tweets added */}
        <Card>
          <CardHeader>
            <CardTitle>Fake Tweets Added Today:</CardTitle>
          </CardHeader>
          <CardContent>{statistics.todayTweetCount}</CardContent>
        </Card>
        {/* User with most number of fake tweets */}
        <Card>
          <CardHeader>
            <CardTitle>User With Most Fake Tweets:</CardTitle>
          </CardHeader>
          <CardContent>{statistics.mostActiveUser.username} - {statistics.mostActiveUser.tweetCount}</CardContent>
        </Card>
      </div>
    </main>
  );
}
