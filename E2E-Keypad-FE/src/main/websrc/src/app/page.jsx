"use client";

import React, { useEffect, useState } from 'react';
import useHook from "@/hooks/useHook";
import Keypad from "@/components/Keyapd.jsx";
import UserInput from '@/components/Userinput';

export default function Page() {
  const { states, actions } = useHook();
  const [inputResult, setInputResult] = useState([]);

  useEffect(() => {
    actions.getJson();
  }, []);

  const handleKeyClick = (key) => {
    console.log('Key clicked:', key);
  };

  const handleResultUpdate = (newResult) => {
    setInputResult(newResult);
  };

  const handleButtonPress = (length, type) => {
    if (type === 'delete_all') {
      handleResultUpdate([]);
    } else if (type === 'backspace') {
      handleResultUpdate(inputResult.slice(0, -1));
    } else {
      setInputResult(prev => [...prev, length]);
    }
  };

  return (
    <div className="App">
      <h1>Custom Keypad</h1>
      <UserInput result={inputResult} />
      <Keypad hash={states.hash} images={states.keypadimage} onButtonPressed={handleButtonPress} />
    </div>
  );
}
