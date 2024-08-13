"use client";

import {useState} from 'react';
import axios from "axios";

export default function useHook() {
  const [hash, setHash] = useState([]);
  const [keypadimage, setKeypadData] = useState(null);

  const getJson = async() => {
    try {
      const res = await axios.get('/api/v1/get_keypad');
      const image = res.data['images'];
      setHash(res.data['keys']);
      setKeypadData(`data:image/png;base64,${image}`);
    } catch(error){
      console.error("키패드 데이터를 가져오는 중 오류 발생:", error);
    }; 
  }

  return {
    states: {
      hash,
      keypadimage
    },
    actions: {
      getJson
    }
  }
}
