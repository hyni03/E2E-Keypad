import React, { useState, useEffect } from 'react';
import JSEncrypt from 'jsencrypt';
import '../style.css';

export default function Keypad({ keymap, images, keypadId, timestamp, hash, onButtonPressed }) {
    const [result, setResult] = useState([]);
    const [publicKey, setPublicKey] = useState('');

    const rows = 3;
    const cols = 4;

    useEffect(() => {
        // public_key.pem 파일을 로드하여 공개키를 설정
        fetch('/public_key.pem')
            .then(response => response.text())
            .then(key => setPublicKey(key))
            .catch(error => console.error('Error loading public key:', error));
    }, []);

    useEffect(() => {
        if (result.length === 6) {
            // alert(result.join(''));
            const encryptedResult = encryptResult(result.join(''));
            if (encryptedResult) {
                const payload = {
                    keyHashMap: keymap,
                    keypadId: keypadId,
                    timestamp: timestamp,
                    userInput: encryptedResult, 
                    hash: hash
                };
                sendToBackend(payload);
            } else {
                console.error('Encryption failed');
            }
            // window.location.reload(); // 페이지 새로고침
        }
    }, [result]);

    // 키에 해당하는 인덱스를 계산하는 함수
    const getKeyIndex = (rowIndex, colIndex) => {
        return rowIndex * cols + colIndex;
    };

    // 버튼 클릭 시 실행되는 함수
    const handleButtonClick = (rowIndex, colIndex) => {
        const index = getKeyIndex(rowIndex, colIndex);
        if (keymap[index] !== " " && keymap[index] !== "  ") {
            setResult([...result, keymap[index]]);
            onButtonPressed && onButtonPressed(result.length + 1, `key${rowIndex + 1}_${colIndex + 1}`);
        }
    };

    const encryptResult = (input) => {
        try {
            // console.log(input);
            const encrypt = new JSEncrypt();
            encrypt.setPublicKey(publicKey);
    
            // Encrypt the input
            const encrypted = encrypt.encrypt(input);
            if (!encrypted) throw new Error('Encryption failed.');
    
            return encrypted; // Already Base64 encoded
        } catch (error) {
            console.error('Encryption error:', error);
            return null;
        }
    };
    
    const sendToBackend = (payload) => {
        fetch('/api/v1/userinput', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
        .then(response => response.json())
        .then(data => {
            // Check if the response body contains an error message
            if (data && data.body && data.body.includes("Keypad ID has expired")) {
                alert("유효시간이 지났습니다. 새로고침하여 다시 시도해주세요.");
                window.location.reload(); // 페이지 새로고침
            } else if (data && data.body && data.body.includes("SUCCESS")) {
                console.log('Success:', data);
            } else {
                throw new Error('Unexpected response: ' + JSON.stringify(data));
            }
        })
        .catch(error => {
            console.error('Error:', error.message);
            alert("오류가 발생했습니다. 다시 시도해주세요.");
        });
    };
    
    
    return (
        <div className="keyboard_wrap">
            <table className="keyboard">
                <tbody>
                    {[...Array(rows)].map((_, rowIndex) => (
                        <tr key={rowIndex}>
                            {[...Array(cols)].map((_, colIndex) => (
                                <td key={`key${rowIndex + 1}_${colIndex + 1}`}>
                                    <button
                                        type="button"
                                        className="key"
                                        onClick={() => handleButtonClick(rowIndex, colIndex)}
                                    >
                                        <span
                                            style={{
                                                backgroundImage: `url(data:${images})`,
                                                backgroundPosition: `-${colIndex * 50}px -${rowIndex * 50}px`
                                            }}
                                        >
                                        </span>
                                    </button>
                                </td>
                            ))}
                        </tr>
                    ))}

                </tbody>
            </table>
        </div>
    );
}
