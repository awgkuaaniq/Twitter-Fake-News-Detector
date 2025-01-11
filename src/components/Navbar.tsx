"use client";

import React, { useState, useEffect } from "react";
import NavSearch from "@/components/NavSearch";

import {
  ArrowLeftStartOnRectangleIcon,
  Bars3Icon,
  CodeBracketSquareIcon,
  Cog6ToothIcon,
  EnvelopeIcon,
} from "@heroicons/react/24/outline";

const Navbar: React.FC = () => {

  return (
    <nav className="bg-gray-100 dark:bg-offblack border-b border-black/15 shadow-lg relative">
      {/* General Container */}
      <div className="flex justify-between max-w-7xl mx-auto items-center px-2 h-fit">
        {/* Logo */}
        <div>
          <a
            href=".."
            className="flex items-center hover:scale-105 transition-all ease-out duration-150"
          >
            <CodeBracketSquareIcon className="size-12 text-black dark:text-white mr-2" />
            <span className="text-black dark:text-white text-xl font-bold">
              VERITAS
            </span>
          </a>
        </div>

        {/* Desktop Navigation - Hidden on mobile */}
        <div className="hidden md:flex text-black dark:text-white justify-between items-center">
          <a
            href="/statistic"
            className="py-5 px-6 hover:bg-gray-200 dark:hover:bg-offgray transition-colors ease-out duration-150"
          >
            Statistics
          </a>
          <a
            href="/manualcheck"
            className="py-5 px-6 hover:bg-gray-200 dark:hover:bg-offgray transition-colors ease-out duration-150"
          >
            Manual Check
          </a>
        </div>

        {/* Desktop Search Bar - Hidden on mobile */}
        <div className="hidden md:block">
          <NavSearch />
        </div>
        </div>
    </nav>
  );
};

export default Navbar;