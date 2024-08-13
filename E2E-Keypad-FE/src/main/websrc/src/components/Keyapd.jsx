import React, { useState, useEffect } from 'react';
import '../style.css';

export default function Keypad({ hash, images, onButtonPressed }) {
    const [result, setResult] = useState([]);

    const rows = 3;
    const cols = 4;

    useEffect(() => {
        if (result.length === 6) {
            alert(result.join(''));
            window.location.reload(); // 페이지 새로고침
        }
    }, [result]);

    // 키에 해당하는 인덱스를 계산하는 함수
    const getHashIndex = (rowIndex, colIndex) => {
        return rowIndex * cols + colIndex;
    };

    // 버튼 클릭 시 실행되는 함수
    const handleButtonClick = (rowIndex, colIndex) => {
        const index = getHashIndex(rowIndex, colIndex);
        if (hash[index] !== " " && hash[index] !== "  ") {
            setResult([...result, hash[index]]);
            onButtonPressed && onButtonPressed(result.length + 1, `key${rowIndex + 1}_${colIndex + 1}`);
        }
    };

    // 전체 삭제 버튼 클릭 시 실행되는 함수
    const handleDeleteAll = () => {
        setResult([]);
        onButtonPressed && onButtonPressed(0, 'delete_all');
    };

    // 백스페이스 버튼 클릭 시 실행되는 함수
    const handleBackspace = () => {
        setResult(result.slice(0, -1));
        onButtonPressed && onButtonPressed(result.length - 1, 'backspace');
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
                    <tr>
                        <td colSpan={2}>
                            <button className="delete_all" onClick={() => handleDeleteAll()}>
                                전체 삭제
                            </button>
                        </td>
                        <td colSpan={2}>
                            <button onClick={() => handleBackspace()}>
                                <svg fill="#3b3b3b" version="1.1" viewBox="0 0 512.01 512.01" stroke="#3b3b3b">
                                    <g id="SVGRepo_bgCarrier" strokeWidth="0"></g>
                                    <g id="SVGRepo_tracerCarrier" strokeLinecap="round" strokeLinejoin="round"></g>
                                    <g id="SVGRepo_iconCarrier">
                                        <g>
                                            <g>
                                                <path d="M490.677,64.005H133.088c-7.189,0-13.888,3.627-17.856,9.643L3.488,244.315c-4.651,7.104-4.651,16.277,0,23.381 l111.744,170.667c3.968,6.016,10.667,9.643,17.856,9.643h357.589c11.797,0,21.333-9.536,21.333-21.333V85.339 C512.011,73.541,502.475,64.005,490.677,64.005z M399.093,356.421c-4.16,4.16-9.621,6.251-15.083,6.251 c-5.461,0-10.923-2.091-15.083-6.251l-70.251-70.251l-70.251,70.251c-4.16,4.16-9.621,6.251-15.083,6.251 c-5.461,0-10.923-2.091-15.083-6.251c-8.341-8.341-8.341-21.824,0-30.165l70.251-70.251l-70.251-70.251 c-8.341-8.341-8.341-21.824,0-30.165s21.824-8.341,30.165,0l70.251,70.251l70.251-70.251c8.341-8.341,21.824-8.341,30.165,0 s8.341,21.824,0,30.165l-70.251,70.251l70.253,70.251C407.434,334.597,407.434,348.08,399.093,356.421z"></path>
                                            </g>
                                        </g>
                                    </g>
                                </svg>	
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    );
}
