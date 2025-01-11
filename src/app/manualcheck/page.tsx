"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Loader2, CheckCircle, XCircle } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import axios from "axios";
import {
  HandThumbDownIcon,
  HandThumbUpIcon,
} from "@heroicons/react/24/outline";

interface CrosscheckResult {
  title: string;
  content: string;
  source: string;
  probability: number;
}

interface ManualCheck {
  id: string;
  query: string;
  checkedAt: string;
  crosscheck: CrosscheckResult;
  feedback?: {
    comment: string;
    like: boolean;
  };
}

export default function FakeNewsChecker() {
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState<ManualCheck | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [feedback, setFeedback] = useState("");
  const [thumbsUp, setThumbsUp] = useState<boolean>();
  const [feedbackError, setFeedbackError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await axios.post<ManualCheck>(
        `http://localhost:8000/api/manual-checks?query=${encodeURIComponent(
          input
        )}`
      );
      setResult(response.data);
      setShowModal(true);
      setFeedback("");
      setFeedbackError("");
      setThumbsUp(undefined);
    } catch (error) {
      console.error("Error:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const checkThumbsUp = (id: string) => {
    setThumbsUp(id === "thumbs_up");
  };

  const sendFeedback = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!feedback || thumbsUp === undefined || !result?.id) {
      setFeedbackError("Please fill the feedback form and like/dislike.");
      return;
    }

    try {
      const feedbackData = {
        comment: feedback,
        like: thumbsUp,
      };

      const response = await axios.post<ManualCheck>(
        `http://localhost:8000/api/manual-checks/${result.id}/feedback`,
        feedbackData
      );

      setResult(response.data);
      setFeedback("");
      setFeedbackError("");
      setThumbsUp(undefined);
      setShowModal(false);
    } catch (error) {
      console.error("Error submitting feedback:", error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4 dark:bg-offblack">
      <Card className="w-full max-w-2xl dark:bg-offgray">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center">
            Fake News Checker
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              placeholder="Enter text to check..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              className="w-full p-2 border rounded dark:bg-gray-700 dark:border-gray-600 dark:text-gray-100 dark:placeholder-gray-200 focus:ring-blue-500 focus:border-transparent"
            />
            <Button
              type="submit"
              className="w-full dark:bg-gray-600 dark:hover:bg-gray-500 dark:text-gray-200"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Checking...
                </>
              ) : (
                "Check"
              )}
            </Button>
          </form>
        </CardContent>
      </Card>

      <Dialog open={showModal} onOpenChange={setShowModal}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle className="flex items-center justify-center text-2xl font-bold">
              {result?.crosscheck && (
                <>
                  {result.crosscheck.probability < 0.5 ? (
                    <CheckCircle className="mr-2 h-6 w-6 text-green-500" />
                  ) : (
                    <XCircle className="mr-2 h-6 w-6 text-red-500" />
                  )}
                  {result.crosscheck.probability < 0.5
                    ? "Likely True"
                    : "Likely Fake"}
                </>
              )}
            </DialogTitle>
          </DialogHeader>
          {result?.crosscheck && (
            <div className="mt-4 space-y-4">
              <h3 className="text-lg font-semibold">Verification Result:</h3>
              <Card className="border-2 border-blue-500">
                <CardContent className="p-4">
                  <h4 className="font-semibold mb-2">
                    {result.crosscheck.title}
                  </h4>
                  <p className="text-sm text-gray-600 mb-2">
                    {result.crosscheck.content}
                  </p>
                  <p className="text-sm text-gray-600 mb-2">
                    Probability:{" "}
                    {(result.crosscheck.probability * 100).toFixed(2)}%
                  </p>
                  <a
                    href={result.crosscheck.source}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-500 hover:underline text-sm"
                  >
                    Read More
                  </a>
                </CardContent>
              </Card>
            </div>
          )}

          <form
            onSubmit={sendFeedback}
            className="flex flex-col w-full space-y-4 h-fit"
          >
            <h1 className="text-lg font-semibold">Feedback:</h1>
            <div className="flex space-x-2">
              <Input
                placeholder="Enter your feedback here"
                value={feedback}
                onChange={(e) => setFeedback(e.target.value)}
              />
              <div className="flex items-start border divide-x-2 divide-gray-300">
                <Button
                  type="button"
                  className={`aspect-square rounded p-1 shadow-md group ${
                    thumbsUp
                      ? "bg-green-600 scale-110 text-white"
                      : "bg-green-500 text-black"
                  } hover:bg-green-600 hover:scale-110 transition ease-out duration-150`}
                  onClick={() => checkThumbsUp("thumbs_up")}
                >
                  <HandThumbUpIcon
                    className={`text-black size-8 group-hover:text-white group-hover:scale-110 
      transition ease-out duration-150 ${
        thumbsUp ? "text-white" : "text-black"
      }`}
                  />
                </Button>
                <Button
                  type="button"
                  className={`aspect-square rounded p-1 shadow-md group ${
                    thumbsUp === false
                      ? "bg-red-600 scale-110 text-white"
                      : "bg-red-500 text-white"
                  } hover:bg-red-600 hover:scale-110 transition ease-out duration-150`}
                  onClick={() => checkThumbsUp("thumbs_down")}
                >
                  <HandThumbDownIcon
                    className={`text-black size-8 group-hover:text-white group-hover:scale-110 
      transition ease-out duration-150 ${
        thumbsUp === false ? "text-white" : "text-black"
      }`}
                  />
                </Button>
              </div>
            </div>
            {feedbackError && (
              <p className="text-sm text-red-500">{feedbackError}</p>
            )}
            <Button type="submit">Submit Feedback</Button>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
