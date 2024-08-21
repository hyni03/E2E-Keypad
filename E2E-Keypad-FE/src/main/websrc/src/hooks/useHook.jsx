"use client";

import {useState} from 'react';
import axios from "axios";

export default function useHook() {
  const [keymap, setKeyMap] = useState([]);
  const [keypadimage, setKeypadData] = useState(null);
  const [keypadId, setKeypadId] = useState('');
  const [timestamp, setTimestamp] = useState('');
  const [hash, setHash] = useState(null);

  const getJson = async () => {
    try {
      const res = await axios.get('/api/v1/get_keypad');
      const image = res.data['images'];
      setKeyMap(res.data['keys']);
      setKeypadData(`data:image/png;base64,${image}`);
      setKeypadId(res.data['keypadId']);
      setTimestamp(res.data['timestamp']);
      setHash(res.data['hash']);
      
    } catch(error){
      console.error("키패드 데이터를 가져오는 중 오류 발생:", error);
    }
  }

  return {
    states: {
      keymap,
      keypadimage,
      keypadId,
      timestamp,
      hash
    },
    actions: {
      getJson
    }
  }
}
