import React, { useState, useRef, useEffect } from "react";

const FloatLabelMultiSelect = ({
  label,
  name,
  value = [],
  onChange,
  id,
  options = [],
  placeholder = "Select options",
  maxDisplayItems = 2,
}) => {
  const [isFocused, setIsFocused] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  const shouldFloat = isFocused || value.length > 0;

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
        setIsFocused(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleOptionToggle = (optionValue) => {
    const newValue = value.includes(optionValue)
      ? value.filter((v) => v !== optionValue)
      : [...value, optionValue];

    onChange({ target: { name, value: newValue } });
  };

  const getDisplayText = () => {
    if (value.length === 0) return "";

    const selectedLabels = value.map((val) => {
      const option = options.find((opt) => opt.value === val);
      return option ? option.label : val;
    });

    if (selectedLabels.length <= maxDisplayItems) {
      return selectedLabels.join(", ");
    }

    return `${selectedLabels.slice(0, maxDisplayItems).join(", ")} +${
      selectedLabels.length - maxDisplayItems
    } more`;
  };

  return (
    <div className="relative w-full m-2" ref={dropdownRef}>
      {/* Display Input */}
      <div
        className="peer w-full border-2 border-slate-300 rounded-lg px-3 pt-3 pb-2 text-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 bg-white cursor-pointer min-h-[2.5rem] flex items-center"
        onClick={() => {
          setIsOpen(!isOpen);
          setIsFocused(true);
        }}
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            setIsOpen(!isOpen);
            setIsFocused(true);
          }
        }}
      >
        <span
          className={value.length === 0 ? "text-gray-400" : "text-gray-900"}
        >
          {getDisplayText() || placeholder}
        </span>
      </div>

      {/* Floating Label */}
      <label
        htmlFor={id || name}
        className={`absolute left-3 px-1 bg-white transition-all duration-200 text-gray-500 ${
          shouldFloat
            ? "top-0 -translate-y-1/2 text-xs text-blue-500"
            : "top-3.5 text-base text-gray-400"
        }`}
        style={{
          pointerEvents: "none",
        }}
      >
        {label}
      </label>

      {/* Dropdown Arrow */}
      <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
        <svg
          className={`w-4 h-4 text-gray-400 transition-transform duration-200 ${
            isOpen ? "rotate-180" : ""
          }`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </div>

      {/* Dropdown Options */}
      {isOpen && (
        <div className="absolute z-50 w-full mt-1 bg-white border-2 border-slate-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
          {options.length === 0 ? (
            <div className="px-3 py-2 text-gray-500 text-sm">
              No options available
            </div>
          ) : (
            options.map((option, index) => (
              <div
                key={index}
                className="flex items-center px-3 py-2 hover:bg-gray-50 cursor-pointer transition-colors"
                onClick={() => handleOptionToggle(option.value)}
              >
                <input
                  type="checkbox"
                  checked={value.includes(option.value)}
                  onChange={() => {}} // Handled by parent div onClick
                  className="mr-3 w-4 h-4 text-blue-500 border-2 border-gray-300 rounded focus:ring-blue-500 focus:ring-2"
                  tabIndex={-1}
                />
                <span className="text-sm text-gray-900 flex-1">
                  {option.label}
                </span>
              </div>
            ))
          )}
        </div>
      )}

      {/* Hidden input for form submission */}
      <input
        type="hidden"
        id={id || name}
        name={name}
        value={JSON.stringify(value)}
      />
    </div>
  );
};

export default FloatLabelMultiSelect;
