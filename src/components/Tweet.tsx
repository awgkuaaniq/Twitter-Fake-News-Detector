import React, { forwardRef } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  InformationCircleIcon,
  LinkIcon,
  NewspaperIcon,
} from "@heroicons/react/24/outline";
import { useState } from "react";
import { formatDate } from "@/utils/formatDate";

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

const truncateBody = (body: string): string => {
  const firstFullStopIndex = body.indexOf(".");
  return firstFullStopIndex !== -1
    ? body.slice(0, firstFullStopIndex + 1)
    : body; // If no full stop is found, return the entire body
};

const Tweet = forwardRef<HTMLDivElement, { tweet: Tweets }>(
  ({ tweet }, ref) => {
    const formattedDate = formatDate(tweet.publishedAt);
    const [showInfo, setShowInfo] = useState(false);

    const toggleInfo = () => {
      setShowInfo((prev) => !prev);
    };

    return (
      <div
        ref={ref}
        className={`flex flex-col h-fit w-full ${
          showInfo ? "bg-gray-200 dark:bg-offgray shadow" : ""
        }transition-colors hover:bg-gray-200 dark:hover:bg-offgray ease-out duration-150`}
      >
        {/* Tweet */}
        <div className="flex min-h-28 h-fit">
          {/* Tweet Icon */}
          <div className="flex flex-col h-full p-2.5">
            <Avatar className="aspect-square w-11 h-11">
              <AvatarImage src={tweet.avatar} alt={`@${tweet.username}`} />
              <AvatarFallback>
                {`${tweet.name[0]}${tweet.name[1]}`}
              </AvatarFallback>
            </Avatar>
          </div>
          {/* Tweet Content */}
          <div className="flex flex-col pr-2.5 w-full h-full">
            {/* Tweet Header */}
            <div className="flex space-x-2.5 px-2.5 pt-2.5 w-full h-fit">
              {/* user  */}
              <div className="flex space-x-1 w-fit h-fit items-center">
                {/* name  */}
                <p className="font-semibold">{tweet.name}</p>
                {/* username  */}
                <p className="font-light text-gray-700 dark:text-gray-400">
                  @{tweet.username}
                </p>
              </div>
              {/* datetime  */}
              <div className="flex space-x-2.5 items-center w-fit h-fit font-light text-gray-700 dark:text-gray-400">
                {/* formatted date and time */}
                <p>{formattedDate}</p>
              </div>
            </div>
            {/* Tweet Body */}
            <div className="flex px-2.5 w-full h-fit">
              <p className="w-fit h-fit">{tweet.body}</p>
            </div>
            {/* Tweet Footer */}
            <div className="flex p-2.5 justify-between content-end w-full h-full">
              <button
                className={`rounded-full p-2 ${
                  showInfo
                    ? "bg-gray-300 dark:bg-sage"
                    : "hover:bg-gray-300 dark:hover:bg-sage transition-colors ease-out duration-150"
                }`}
                onClick={toggleInfo}
                title="Crosschecking Sources"
              >
                <InformationCircleIcon className="w-6 h-6" />
              </button>
              <div className="flex space-x-5">
                <a
                  className="hover:bg-gray-300 rounded-full p-2 dark:hover:bg-sage transition-colors ease-out duration-150"
                  title="Tweet URL"
                  href={tweet.tweetUrl}
                  target="_blank"
                >
                  <LinkIcon className="w-6 h-6" />
                </a>
              </div>
            </div>
          </div>
        </div>
        {/* Tweet Info  */}
        {showInfo && tweet.crosscheck && (
          <div className="flex flex-col pb-8 justify-center items-center md:px-20 w-full h-fit">
            {/* Card  */}
            <div className="flex flex-col shadow-2xl rounded-3xl w-full">
              {/* Info Header  */}
              <div className="flex w-full justify-center rounded-t-3xl py-2.5 h-fit px-2.5 mx-auto bg-gray-100 dark:bg-offblack">
                {/* Source  */}
                <p className="font-semibold">Source</p>
              </div>
              {/* Info Body  */}
              <div className="flex w-full h-fit p-2.5">
                <div className="md:flex-row flex flex-col w-full h-fit p-3 gap-x-3 items-center">
                  <h1
                    className="text-xl text-nowrap font-semibold"
                    title="Probability of fake news"
                  >
                    Probability:
                  </h1>
                  <h1 className="text-4xl w-fit text-green-500 font-bold">
                    {(tweet.crosscheck.probability * 100).toFixed(2)}%
                  </h1>
                  <div className="flex flex-col">
                    <a
                      className="text-base font-semibold w-full hover:text-sky-600"
                      href={tweet.crosscheck.source}
                      target="_blank"
                    >
                      {tweet.crosscheck.title}
                    </a>
                    <h2 className="text-sm font-light text-justify">
                      {truncateBody(tweet.crosscheck.content)}
                    </h2>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }
);
Tweet.displayName = "Tweet";
export default Tweet;
