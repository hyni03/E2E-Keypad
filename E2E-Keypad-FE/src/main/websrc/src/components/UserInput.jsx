import React from 'react';
import '../style.css';

export default function UserInput({ result }) {
    const maxLength = 6;

    return (
        <div className="user-input">
            {[...Array(maxLength)].map((_, index) => (
                <span key={index} className={`circle ${result.length > index ? 'filled' : ''}`}></span>
            ))}
        </div>
    );
}
