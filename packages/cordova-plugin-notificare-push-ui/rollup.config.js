import commonjs from 'rollup-plugin-commonjs';
import resolve from 'rollup-plugin-node-resolve';
import typescript from 'rollup-plugin-typescript2';

export default [
  {
    input: 'src/js/index.ts',
    output: {
      file: 'build/notificare-push-ui.bundle.js',
      format: 'cjs',
      exports: 'default',
      sourcemap: true,
    },
    plugins: [
      typescript({
        tsconfig: 'tsconfig.json',
        tsconfigOverride: {
          compilerOptions: {
            declaration: true,
            declarationMap: true,
            module: 'ES2015',
          },
        },
      }),
      resolve({
        mainFields: ['module'],
      }),
      commonjs(),
    ],
  },
];
