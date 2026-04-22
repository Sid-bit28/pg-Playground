import {
  createAsyncThunk,
  createSlice,
  type PayloadAction,
} from '@reduxjs/toolkit';
import axios from 'axios';

// Defining TypeScript Interfaces based on our Java DTOs
export interface TableMetaData {
  tableName: string;
  columns: string[];
}

export interface QueryResult {
  columns: string[] | null;
  rows: Record<string, any>[] | null;
  error: string | null;
  executionTimeMs: number;
}

interface EditorSlice {
  schema: TableMetaData[];
  currentQuery: string;
  result: QueryResult | null;
  isExecuting: boolean;
  isSchemaLoading: boolean;
}

// Defining the initial state of the parameters defined.
const initialState: EditorSlice = {
  schema: [],
  currentQuery:
    '-- Write your PostgreSQL queries here\n SELECT * from employees;',
  result: null,
  isExecuting: false,
  isSchemaLoading: false,
};

// Async Thunk to communicate to Springboot
export const fetchSchema = createAsyncThunk('editor/fetchSchema', async () => {
  const response = await axios.get('http://localhost:8080/api/sql/schema');
  return response.data;
});

export const executeQuery = createAsyncThunk(
  'editor/executeQuery',
  async (query: string) => {
    const response = await axios.post('http://localhost:8080/api/sql/execute', {
      query,
    });
    return response.data;
  },
);

const editorSlice = createSlice({
  name: 'editor',
  initialState,
  reducers: {
    setQuery: (state, action: PayloadAction<string>) => {
      state.currentQuery = action.payload;
    },
    clearResults: state => {
      state.result = null;
    },
  },
  extraReducers: builder => {
    //Schema fetching lifecycle
    builder.addCase(fetchSchema.pending, state => {
      state.isSchemaLoading = true;
    });
    builder.addCase(fetchSchema.fulfilled, (state, action) => {
      state.isSchemaLoading = false;
      state.schema = action.payload;
    });
    builder.addCase(fetchSchema.rejected, state => {
      state.isSchemaLoading = false;
    });

    //Query executing lifecycle
    builder.addCase(executeQuery.pending, state => {
      state.isExecuting = true;
    });
    builder.addCase(executeQuery.fulfilled, (state, action) => {
      state.isExecuting = false;
      state.result = action.payload;
    });
    builder.addCase(executeQuery.rejected, (state, action) => {
      state.isExecuting = false;
      state.result = {
        columns: null,
        rows: null,
        error: action.error.message || 'Network Error',
        executionTimeMs: 0,
      };
    });
  },
});

export const { setQuery, clearResults } = editorSlice.actions;
export default editorSlice.reducer;
